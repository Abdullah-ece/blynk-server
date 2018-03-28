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

import {Preview} from './scenes';

import Validation from 'services/Validation';

import {
  WIDGETS_SWITCH_ALIGNMENT
} from 'services/Widgets';

import {
  Select as AntdSelect,
  Switch,
  Row,
  Col,
  Radio,
} from 'antd';

import {
  reduxForm,
  getFormValues,
  Field,
  change,
  reset,
  initialize,
} from 'redux-form';

import WidgetSettings from '../WidgetSettings';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

import './styles.less';
import {WIDGETS_SWITCH_LABEL_ALIGNMENT} from "services/Widgets/index";

@connect((state, ownProps) => ({
  formValues: getFormValues(ownProps.form)(state) || {},
  dataStreams: state.Product.edit.dataStreams.fields || [],
}), (dispatch) => ({
  changeForm: bindActionCreators(change, dispatch),
  resetForm: bindActionCreators(reset, dispatch),
  initializeForm: bindActionCreators(initialize, dispatch),
  // destroyForm: bindActionCreators(destroy, dispatch),
}))
@reduxForm()
class SwitchSettings extends React.Component {

  static propTypes = {

    dataStreams: PropTypes.array,

    form: PropTypes.string,
    deviceId: PropTypes.number,
    formValues: PropTypes.object,
    initialValues: PropTypes.object,

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
    this.labelPositionComponent = this.labelPositionComponent.bind(this);
    this.sourceMultipleSelectComponent = this.sourceMultipleSelectComponent.bind(this);
  }

  componentWillUpdate(nextProps) {
    if(!nextProps.visible && this.props.visible !== nextProps.visible) {
      // this.props.clearWidgetDevicePreviewHistory();
      // this.props.resetForm('bar-chart-widget-preview');
    }

    if (!_.isEqual(nextProps.initialValues, this.props.initialValues)) {
      this.props.initializeForm(nextProps.form, nextProps.initialValues);
    }
  }

  colorPalette = [
    '#000',
    '#24c48e',
    '#007dc4',
    '#04c0f8',
    '#d3435c',
    '#ea7d26',
    '#e92126',
  ];

  colorPickerComponent({input}) {
    return (
      <ColorPicker width={`240px`}
                   colors={this.colorPalette}
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

      if(!value)
        return props.changeForm(this.props.form, 'sources.0.dataStream', null);

      const getStreamByPin = (pin) => {
        return _.find(props.dataStreams, (stream) => {
          return parseInt(stream.values.pin) === parseInt(pin);
        });
      };

      let dataStream = getStreamByPin(value);

      if(!dataStream)
        return props.changeForm(this.props.form, 'sources.0.dataStream', {
          pin: value,
          pinType: "VIRTUAL",
        });

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

  labelPositionComponent(props) {
    return (
      <Radio.Group onChange={props.input.onChange} value={props.input.value} className={`modal-window-widget-settings-config--switch-text-alignment`}>
        <Radio value={WIDGETS_SWITCH_LABEL_ALIGNMENT.LEFT}>
          Left
        </Radio>
        <Radio value={WIDGETS_SWITCH_LABEL_ALIGNMENT.RIGHT}>
          Right
        </Radio>
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

  render() {

    const sourcesOptions = this.getSourceOptions();

    return (
      <WidgetSettings
        visible={this.props.visible}
        onSave={this.handleSave}
        onCancel={this.handleCancel}
        preview={(
          <Preview  deviceId={this.props.deviceId}
                    data={this.props.formValues}/>
        )}
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

              { this.props.formValues.sources && this.props.formValues.sources.length && this.props.formValues.sources[0].dataStream && (

                <div>

                  <Row>
                    <Col span={8}>
                      <Item label="on value" offset="medium" displayError={false}>
                        <FormField name={'onValue'} placeholder={`For example: 1`} validate={[Validation.Rules.required]}/>
                      </Item>
                    </Col>
                    <Col span={8} offset={1}>
                      <Item label="off value" offset="medium" displayError={false}>
                        <FormField name={'offValue'} placeholder={`For example: 0`} validate={[Validation.Rules.required]}/>
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
                      <Item label={'Color'} offset={'medium'} className={`widgets--label-widget--switch-settings-background-color-picker`}>
                        <Field name={'color'} component={this.colorPickerComponent} />
                      </Item>
                    </Col>
                  </Row>

                  <Item offset={'normal'}>
                    <Field name={'isSwitchLabelsEnabled'} component={this.switchComponent} label={'Show on/off labels'}/>
                  </Item>

                  {this.props.formValues.isSwitchLabelsEnabled && (

                    <div>

                      <Row>
                        <Col span={8}>
                          <Item label="on label" offset="medium" displayError={false}>
                            <FormField name={'onLabel'} placeholder={`For e.g. On`}/>
                          </Item>
                        </Col>
                        <Col span={8} offset={1}>
                          <Item label="off label" offset="medium" displayError={false}>
                            <FormField name={'offLabel'} placeholder={`For e.g. Off`}/>
                          </Item>
                        </Col>
                      </Row>

                      <Item label="label position" offset="normal">
                        <Field name={'labelPosition'} component={this.labelPositionComponent}/>
                      </Item>

                      <Item offset={'normal'}>
                        <Field name={'isWidgetNameHidden'} component={this.switchComponent} label={'Hide widget name'}/>
                      </Item>

                    </div>

                  )}

                </div>
              ) || null}

            </div>

          </div>
        )}
      />
    );
  }

}

export default SwitchSettings;
