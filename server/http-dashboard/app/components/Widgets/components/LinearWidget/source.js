import React from 'react';
import {MetadataSelect as Select} from 'components/Form';
import ColorPicker from 'components/ColorPicker';
import {Item, ItemsGroup} from "components/UI";
import {Button, Radio, Input, Icon, Row, Col, Select as AntdSelect, Switch} from 'antd';
import {Field, change} from 'redux-form';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import PropTypes from 'prop-types';
import Validation from 'services/Validation';
import {Map, List} from 'immutable';
import _ from 'lodash';
import {
  WIDGETS_SOURCE_TYPES_LIST,
  WIDGETS_CHART_TYPES_LIST,
  WIDGETS_CHART_TYPES,
} from 'services/Widgets';
import {
  SimpleContentEditable
} from 'components';

@connect(() => ({}), (dispatch) => ({
  changeForm: bindActionCreators(change, dispatch)
}))
class Source extends React.Component {

  static propTypes = {
    form: PropTypes.string,

    index: PropTypes.number,

    source: PropTypes.instanceOf(Map),
    dataStreams: PropTypes.instanceOf(List),

    dataStream: PropTypes.instanceOf(Map),

    isAbleToDelete: PropTypes.bool,

    onCopy: PropTypes.func,
    onChange: PropTypes.func,
    onDelete: PropTypes.func,
    changeForm: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleCopy = this.handleCopy.bind(this);
    this.handleDelete = this.handleDelete.bind(this);
    this.labelComponent = this.labelComponent.bind(this);
    this.colorPickerComponent = this.colorPickerComponent.bind(this);
    this.dataStreamSelectComponent = this.dataStreamSelectComponent.bind(this);
  }

  componentDidUpdate(prevProps) {

    if( (!prevProps.dataStream && this.props.dataStream) || (prevProps.dataStream.get('id') !== this.props.dataStream.get('id')) ) {

      const source = this.props.source
        .set('min', this.props.dataStream.get('min'))
        .set('max', this.props.dataStream.get('max'))
        .toJS();

      this.props.changeForm(
        this.props.form,
        `sources.${this.props.index}`,
        source
      );

    }

  }

  colorPalette = [
    '#000',
    '#fff',
    '#58595d',
    '#24c48e',
    '#04c0f8',
    '#d3435c',
    '#ea7d26',
    '#e92126',
  ];

  minMaxComponent({input, placeholder}) {
    return (
      <Input placeholder={placeholder} value={input.value} onChange={input.onChange}/>
    );
  }

  switchComponent(props) {
    return (
      <div>
        <Switch size="small" onChange={props.input.onChange} checked={Boolean(props.input.value)}/>
        <span className="switch-label font-size-medium">
          { props.label }
        </span>
      </div>

    );
  }

  getIconForChartByType(type) {
    if (type === WIDGETS_CHART_TYPES.LINE)
      return 'line-chart';

    if (type === WIDGETS_CHART_TYPES.AREA)
      return 'area-chart';

    // dot-chart bar-chart

  }

  labelComponent({input}) {
    return (
      <SimpleContentEditable
        maxLength={30}
        className="modal-window-widget-settings-config-column-sources-source-header-name"
        value={input.value}
        onChange={input.onChange}/>
    );
  }

  chartTypeSelectComponent({input, getIconForChartByType}) {
    return (
      <Radio.Group value={input.value} onChange={input.onChange}>
        {WIDGETS_CHART_TYPES_LIST.map((chart) => (
          <Radio.Button value={chart.key} key={chart.key}>
            <Icon type={getIconForChartByType(chart.key)}/>
          </Radio.Button>
        ))}
      </Radio.Group>
    );
  }

  colorPickerComponent({input}) {
    return (
      <ColorPicker colors={this.colorPalette} title="primary color" color={input.value} style={{marginTop: '-1px'}}
                   onChange={input.onChange}/>
    );
  }

  handleDelete() {

    if(this.props.onDelete)
      this.props.onDelete(this.props.source.get('id'));
  }

  handleCopy() {
    if(this.props.onCopy)
      this.props.onCopy(this.props.source.get('id'));
  }

  dataStreamSelectComponent(props) {

    const onChange = (value) => {

      const stream = this.props.dataStreams.find(
        stream => parseInt(stream.get('key')) === parseInt(value)
      );

      if (stream) {
        props.input.onChange(stream.get('values').toJS());
      } else {
        props.input.onChange({});
      }

    };

    const getValue = () => {
      if (props.input.value && !isNaN(Number(props.input.value.pin)) && props.input.value.pinType === 'VIRTUAL')
        return String(props.input.value.pin);

      return '';
    };

    return this.multipleTagsSelect({
      ...props,
      input: {
        ...props.input,
        value: getValue(),
        onChange: onChange,
        onBlur: () => {},
      }
    });

  }

  multipleTagsSelect(props) {

    const getOption = (item) => {
      return (
        <AntdSelect.Option value={item.key} key={item.key} disabled={item.disabled || false}>
          {item.value}
        </AntdSelect.Option>
      );
    };

    const getOptions = (list) => {
      return list.map((item) => getOption(item));
    };

    const getGroup = (name, list) => {
      return (
        <AntdSelect.OptGroup key={name}>
          {getOptions(list)}
        </AntdSelect.OptGroup>
      );
    };

    const getGroups = (list) => {
      return Object.keys(list).map((key) => getGroup(key, list[key]));
    };

    const values = props.values || [];

    let optionsList = null;

    if (Array.isArray(values)) {
      optionsList = getOptions(values);
    } else {
      optionsList = getGroups(values);
    }

    return (
      <AntdSelect onFocus={props.input.onFocus}
                  onBlur={props.input.onBlur}
                  onChange={props.input.onChange}
                  notFoundContent={props.notFoundContent || ''}
                  dropdownMatchSelectWidth={props.dropdownMatchSelectWidth || false}
                  value={props.input.value || []}
                  placeholder={props.placeholder || ''}
                  validate={props.validate || []}
                  filterOption={props.filterOption || undefined}
                  style={{width: '100%'}}
                  allowClear={true}>
        {optionsList || null}
      </AntdSelect>
    );

  }

  render() {

    const getNotFoundDataStreamContent = () => {

      if (this.props.dataStreams.length) return 'No Data Streams match your request';

      return 'Add Data Stream before setup chart';
    };

    // #849

    const getLabelForChartTypeItem = () => {

      const name = _.find(WIDGETS_CHART_TYPES_LIST, ((item) => item.key === this.props.source.get('graphType')));

      return `Chart Type: ${name && name.value || 'None'}`;
    };

    return (
      <div className="modal-window-widget-settings-config-column-sources-source">
        <div className="modal-window-widget-settings-config-column-sources-source-header">
          <Field name={`sources.${this.props.index}.label`} component={this.labelComponent}/>

          <div className="modal-window-widget-settings-config-column-sources-source-header-tools">
            { this.props.isAbleToDelete && (
              <Button size="small" icon="delete" onClick={this.handleDelete}/>
            )}
            {/*<Button size="small" icon="copy" onClick={this.handleCopy}/>*/} { /* uncomment when start to support multiple sources*/}
            {/*<Button size="small" icon="bars" disabled={true}/>*/}
          </div>
        </div>
        <div className="modal-window-widget-settings-config-column-sources-source-type-select">
          <ItemsGroup>
            <Item label="Source" offset="medium">
              <Select dropdownMatchSelectWidth={false}
                      name={`sources.${this.props.index}.sourceType`}
                      values={WIDGETS_SOURCE_TYPES_LIST}
                      placeholder="Choose Type"
                      validate={[Validation.Rules.required]}
                      style={{width: '100px'}}/>
            </Item>
            <Item label=" " offset="medium" style={{width: '100%'}}>

              <Field name={`sources.${this.props.index}.dataStream`}
                     notFoundContent={getNotFoundDataStreamContent()}
                     dropdownMatchSelectWidth={false}
                     values={{ 'Data Streams': this.props.dataStreams.toJS()}}
                     placeholder="Choose Source"
                     validate={[Validation.Rules.required]}
                     style={{width: '100%'}}

                     component={this.dataStreamSelectComponent}
              />

            </Item>
          </ItemsGroup>
        </div>

        <div className="modal-window-widget-settings-config-column-sources-source-chart-type">
          <div className="modal-window-widget-settings-config-column-sources-source-chart-type-select">
            <Row>
              <Col span={8}>
                <Item label={getLabelForChartTypeItem()} offset="medium">
                  <Field component={this.chartTypeSelectComponent} name={`sources.${this.props.index}.graphType`}
                         getIconForChartByType={this.getIconForChartByType}/>
                </Item>
              </Col>
              <Col span={12}>
                <Item label="Color" offset="medium">
                  <Field component={this.colorPickerComponent} name={`sources.${this.props.index}.color`}
                         getIconForChartByType={this.getIconForChartByType}/>
                </Item>

              </Col>
            </Row>
          </div>
        </div>

        <Item label="Display separate Y axis" offset="medium">
          <Field name={`sources.${this.props.index}.enableYAxis`} component={this.switchComponent} label={this.props.source.get('enableYAxis') ? 'Enabled' : 'Disabled'}/>
        </Item>

        <Item label="Autoscale" offset="small">
          <Field name={`sources.${this.props.index}.autoscale`} component={this.switchComponent} label={this.props.source.get('autoscale') ? 'Enabled' : 'Disabled'}/>
        </Item>

        { !this.props.source.get('autoscale') && (

          <Row>
            <Col span={6}>
              <Item label="Min">
                <Field component={this.minMaxComponent} name={`sources.${this.props.index}.min`} placeholder={isNaN(Number(this.props.dataStream.get('min'))) ? '0' : this.props.dataStream.get('min')}/>
              </Item>
            </Col>
            <Col span={6} offset={2}>
              <Item label="Max">
                <Field component={this.minMaxComponent} name={`sources.${this.props.index}.max`} placeholder={isNaN(Number(this.props.dataStream.get('max'))) ? '100' : this.props.dataStream.get('max')}/>
              </Item>
            </Col>
          </Row>

        ) || null }

      </div>
    );
  }

}

export default Source;
