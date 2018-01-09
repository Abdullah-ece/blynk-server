import React from 'react';

import {
  SimpleContentEditable,
  FontAwesome,
  ColorPicker,
} from 'components';

import {
  Item,
} from "components/UI";

import _ from 'lodash';

import {
  Field as FormField,
} from 'components/Form';



import Validation from 'services/Validation';

import {
  WIDGETS_SWITCH_ALIGNMENT
} from 'services/Widgets';

import {
  Select as AntdSelect,
  Row,
  Col,
  Radio,
} from 'antd';

import {
  reduxForm,
  getFormValues,
  Field,
  change
} from 'redux-form';

import WidgetSettings from '../WidgetSettings';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

import './styles.less';

@connect((state, ownProps) => ({
  formValues: getFormValues(ownProps.form)(state) || {},
  dataStreams: state.Product.edit.dataStreams.fields || [],
}), (dispatch) => ({
  changeForm: bindActionCreators(change, dispatch),
  // resetForm: bindActionCreators(reset, dispatch),
  // initializeForm: bindActionCreators(initialize, dispatch),
  // destroyForm: bindActionCreators(destroy, dispatch),
}))
@reduxForm()
class SwitchSettings extends React.Component {

  static propTypes = {

    dataStreams: PropTypes.array,

    form: PropTypes.string,
    formValues: PropTypes.object,

    visible: PropTypes.bool,

    onClose: PropTypes.func,
    resetForm: PropTypes.func,
    changeForm: PropTypes.func,
    destroyForm: PropTypes.func,
    handleSubmit: PropTypes.func,
    initializeForm: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleSave = this.handleSave.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.multipleTagsSelect = this.multipleTagsSelect.bind(this);
    this.colorPickerComponent = this.colorPickerComponent.bind(this);
    this.labelNameComponent = this.labelNameComponent.bind(this);
    this.textAlignmentComponent = this.textAlignmentComponent.bind(this);
    this.sourceMultipleSelectComponent = this.sourceMultipleSelectComponent.bind(this);
  }

  colorPalette = [
    '#000',
    '#fff',
    '#24c48e',
    '#04c0f8',
    '#d3435c',
    '#ea7d26',
    '#ea7d26',
  ];

  colorPickerComponent({input}) {
    return (
      <ColorPicker colors={this.colorPalette}
                   title="primary color" color={input.value}
                   onChange={input.onChange}/>
    );
  }

  handleCancel() {
    if (typeof this.props.onClose === 'function')
      this.props.onClose();

    // this.props.resetForm(this.props.form);
  }

  handleSave() {
    if(typeof this.props.handleSubmit === 'function')
      this.props.handleSubmit();
  }

  labelNameComponent({input}) {
    return (
      <SimpleContentEditable maxLength={35}
                             className="modal-window-widget-settings-config-widget-name"
                             value={input.value}
                             onChange={input.onChange}/>
    );
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
      <AntdSelect showSearch
                  onFocus={props.input.onFocus}
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

  sourceMultipleSelectComponent(props) {

    const onChange = (value) => {

      const getStreamByPin = (pin) => {
        return _.find(props.dataStreams, (stream) => {
          return parseInt(stream.values.pin) === parseInt(pin);
        });
      };

      let dataStream = getStreamByPin(value);

      if(!dataStream)
        return props.changeForm(this.props.form, 'sources.0.dataStream', null);

      props.changeForm(this.props.form, 'sources.0.dataStream', dataStream.values);

    };

    const getValue = () => {

      if(!props.input.value) return [];

      let pin = this.props.formValues.sources[0].dataStream.pin;

      let value = String(pin);

      return value;
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

  textAlignmentComponent(props) {
    return (
      <Radio.Group onChange={props.input.onChange} value={props.input.value} className={`modal-window-widget-settings-config--switch-text-alignment`}>
        <Radio.Button value={WIDGETS_SWITCH_ALIGNMENT.LEFT}>
          <FontAwesome name={'align-left'}/>
        </Radio.Button>
        <Radio.Button value={WIDGETS_SWITCH_ALIGNMENT.CENTER}>
          <FontAwesome name={'align-center'}/>
          {/*{ FormatAlignCenter }*/}
        </Radio.Button>
      </Radio.Group>
    );
  }

  getSourceOptions() {
    const dataStreamsOptions = [];

    const pinsDescribedOnDataStreams = [];

    this.props.dataStreams.forEach((stream) => {

      // collect streams described on Data Streams

      dataStreamsOptions.push({
        key: `${stream.values.pin}`,
        value: `${stream.values.label}`,
      });

      pinsDescribedOnDataStreams.push(stream.values.pin);

      /* Uncomment when need tableDescription fields listed on Dropdown */

      // if (stream.values.tableDescriptor && stream.values.tableDescriptor.columns) {
      //   stream.values.tableDescriptor.columns.forEach((column) => {
      //     dataStreamsOptions.push({
      //       key: `${stream.values.pin}|${column.label}`,
      //       value: `-- ${column.label}`,
      //     });
      //   });
      // }

      /* END */

    });

    // Collect other PIN's from 0 to 127 except already existed on Data Streams

    for(let i = 0; i <= 127; i++) {

      if(pinsDescribedOnDataStreams.indexOf(i) >= 0)
        continue;

      dataStreamsOptions.push({
        key: `${i}`,
        value: `V${i}`,
      });
    }

    return dataStreamsOptions;
  }

  render() {

    const sourcesOptions = this.getSourceOptions();

    return (
      <WidgetSettings
        visible={this.props.visible}
        onSave={this.handleSave}
        onCancel={this.handleCancel}
        config={(
          <div>
            <div className="modal-window-widget-settings-config-column-header">
              <Field name="label" component={this.labelNameComponent}/>

              <div className="modal-window-widget-settings-config-add-source">
                {/*<Button type="dashed" onClick={this.handleAddSource}>Add source</Button>*/}
              </div>
            </div>

            <div className="modal-window-widget-settings-config-column-bar-configuration">

              <Item label="Target" offset="large">
                <Field name="sources.0.dataStream"
                       style={{width: '100%'}}
                       placeholder="Choose Target"
                       validate={[Validation.Rules.required]}
                       component={this.sourceMultipleSelectComponent}
                       values={sourcesOptions}
                       dataStreams={this.props.dataStreams}
                       formValues={this.props.formValues}
                       changeForm={this.props.changeForm}
                       form={this.props.form}
                       filterOption={this.simpleMatch}
                       notFoundContent={sourcesOptions.length > 0 ?
                         "No DataStreams or PINs match search":"Create at least one DataStream"}
                />
              </Item>

              <Row>
                <Col span={8}>
                  <Item label="on value" offset="medium">
                    <FormField name={'onValue'} placeholder={`For example: 1`}/>
                  </Item>
                </Col>
                <Col span={8} offset={1}>
                  <Item label="off value" offset="medium">
                    <FormField name={'offValue'} placeholder={`For example: 0`}/>
                  </Item>
                </Col>
              </Row>

              <Row>
                <Col span={8}>
                  <Item label={'Alignment'} offset={'medium'}>
                    <Field name={'alignment'} component={this.textAlignmentComponent} />
                  </Item>
                </Col>
                <Col span={8} offset={1}>
                  <Item label={'Color'} offset={'medium'} className={`widgets--label-widget--settings-background-color-picker`}>
                    <Field name={'color'} component={this.colorPickerComponent} />
                  </Item>
                </Col>
              </Row>
            </div>

          </div>
        )}
      />
    );
  }

}

export default SwitchSettings;
