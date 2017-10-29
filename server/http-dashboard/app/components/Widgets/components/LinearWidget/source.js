import React from 'react';
import {MetadataSelect as Select} from 'components/Form';
import ColorPicker from 'components/ColorPicker';
import {Item, ItemsGroup} from "components/UI";
import {Button, Radio, Icon, Row, Col} from 'antd';
import {Field, change} from 'redux-form';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import PropTypes from 'prop-types';
import Validation from 'services/Validation';
import {Map, List} from 'immutable';
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

    onCopy: PropTypes.func,
    onDelete: PropTypes.func,
    changeForm: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleCopy = this.handleCopy.bind(this);
    this.handleDelete = this.handleDelete.bind(this);
    this.labelComponent = this.labelComponent.bind(this);
  }

  componentWillUpdate(nextProps) {
    if (Number(nextProps.source.get('dataStreamPin')) && Number(nextProps.source.get('dataStreamPin')) !== Number(this.props.source.get('dataStreamPin')) && nextProps.dataStreams.size) {
      const stream = this.props.dataStreams.find(
        stream => parseInt(stream.get('key')) === parseInt(nextProps.source.get('dataStreamPin'))
      );

      if (stream) {
        this.props.changeForm(this.props.form, `sources.${this.props.index}.dataStream`, stream.get('values'));
      }
    }

    const dataStreamNotFound = !nextProps.dataStreams.size || !nextProps.dataStreams.find(stream => {
      return Number(stream.get('key')) === Number(nextProps.source.get('dataStreamPin'));
    });

    if (dataStreamNotFound) {
      this.props.changeForm(this.props.form, `sources.${this.props.index}.dataStream`, {});
      this.props.changeForm(this.props.form, `sources.${this.props.index}.dataStreamPin`, null);
    }
  }

  getIconForChartByType(type) {
    if (type === WIDGETS_CHART_TYPES.LINE)
      return 'line-chart';

    if (type === WIDGETS_CHART_TYPES.DOTS)
      return 'dot-chart';

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
      <ColorPicker title="primary color" color={input.value}
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

  render() {

    const getNotFoundDataStreamContent = () => {

      if (this.props.dataStreams.length) return 'No Data Streams match your request';

      return 'Add Data Stream before setup chart';
    };

    // #849

    // const getLabelForChartTypeItem = () => {
    //
    //   const name = _.find(WIDGETS_CHART_TYPES_LIST, ((item) => item.key === this.props.source.get('graphType')));
    //
    //   if (!name) return `Please, select chart type`;
    //
    //   return `Chart Type: ${name.value}`;
    // };

    return (
      <div className="modal-window-widget-settings-config-column-sources-source">
        <div className="modal-window-widget-settings-config-column-sources-source-header">
          <Field name={`sources.${this.props.index}.label`} component={this.labelComponent}/>

          <div className="modal-window-widget-settings-config-column-sources-source-header-tools">
            <Button size="small" icon="delete" onClick={this.handleDelete}/>
            {/*<Button size="small" icon="copy" onClick={this.handleCopy}/>*/} { /* uncomment when start to support multiple sources*/}
            <Button size="small" icon="bars" disabled={true}/>
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
            <Item label=" " offset="medium">
              <Select notFoundContent={getNotFoundDataStreamContent()}
                      dropdownMatchSelectWidth={false}
                      name={`sources.${this.props.index}.dataStreamPin`} values={{ 'Data Streams': this.props.dataStreams.toJS()}}
                      placeholder="Choose Source"
                      validate={[Validation.Rules.required]}
                      style={{width: '100%'}}/>
            </Item>
          </ItemsGroup>
        </div>

        <div className="modal-window-widget-settings-config-column-sources-source-chart-type">
          <div className="modal-window-widget-settings-config-column-sources-source-chart-type-select">
            <Row>
              {/*#849 Temporary hide this element*/}

              {/*<Col span={6}>*/}
                {/*<Item label={getLabelForChartTypeItem()} offset="medium">*/}
                  {/*<Field component={this.chartTypeSelectComponent} name={`sources.${this.props.index}.graphType`}*/}
                         {/*getIconForChartByType={this.getIconForChartByType}/>*/}
                {/*</Item>*/}
              {/*</Col>*/}
              <Col span={12}>
                <Item label="Color" offset="medium">
                  <Field component={this.colorPickerComponent} name={`sources.${this.props.index}.color`}
                         getIconForChartByType={this.getIconForChartByType}/>
                </Item>

              </Col>
            </Row>
          </div>
        </div>

      </div>
    );
  }

}

export default Source;
