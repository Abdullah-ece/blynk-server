import React from 'react';
import './styles.less';
import WidgetSettings from '../WidgetSettings';
import PropTypes from 'prop-types';
import {reduxForm, getFormValues, reset, initialize, destroy, change, Field} from 'redux-form';
import {connect} from 'react-redux';
import {Preview} from './scenes';
import {bindActionCreators} from 'redux';
import _ from 'lodash';
import {
  WIDGETS_SLIDER_VALUE_POSITION, WIDGETS_SOURCE_TYPES_LIST,
} from 'services/Widgets';
import Validation from 'services/Validation';
import {
  Field as FormField, MetadataSelect as Select,
} from 'components/Form';
import {
  Select as AntdSelect,
  Radio,
  Row,
  Col,
  Switch,
} from 'antd';
import {
  ColorPicker,
  SimpleContentEditable,
} from 'components';
import {
  Item, ItemsGroup,
} from "components/UI";
import {
  Map,
} from 'immutable';
import {FORMS} from "services/Products";

@connect((state, ownProps) => ({
  formValues: (getFormValues(ownProps.form)(state) || {}),
  dataStreams: (() => {
    const formValues = getFormValues(FORMS.PRODUCTS_PRODUCT_MANAGE)(state);

    return (formValues && formValues.dataStreams || []);
  })(),
}), (dispatch) => ({
  changeForm: bindActionCreators(change, dispatch),
  resetForm: bindActionCreators(reset, dispatch),
  initializeForm: bindActionCreators(initialize, dispatch),
  destroyForm: bindActionCreators(destroy, dispatch),
}))
@reduxForm({
  validate: (values) => {
    const errors = {};

    if(!isNaN(Number(values.minValue)) && !isNaN(Number(values.maxValue)) && Number(values.minValue) >= Number(values.maxValue)) {
      errors.minValue = 'Min value should be less than Max value';
      errors.maxValue = 'Max value should be more than Min value';
    }

    if(!isNaN(Number(values.step)) && !isNaN(Number(values.minValue)) && !isNaN(Number(values.maxValue)) && (values.step > (values.maxValue - values.minValue))) {
      errors.step = 'Step value is out of range';
    }

    if(!isNaN(Number(values.step)) && values.step <= 0) {
      errors.step = 'Step value is out of range';
    }

    if(!isNaN(Number(values.step)) && !isNaN(Number(values.minValue)) && !isNaN(Number(values.maxValue)) && (values.fineControlStep > (values.maxValue - values.minValue))) {
      errors.fineControlStep = 'Fine Control Step value is out of range';
    }

    if(!isNaN(Number(values.fineControlStep)) && values.fineControlStep <= 0) {
      errors.fineControlStep = 'Step value is out of range';
    }

    return errors;
  }
})
class SliderWidgetSettings extends React.Component {

  static propTypes = {
    visible: PropTypes.bool,

    onClose: PropTypes.func,
    resetForm: PropTypes.func,
    changeForm: PropTypes.func,
    handleSubmit: PropTypes.func,

    loading: PropTypes.oneOfType([
      PropTypes.bool,
      PropTypes.object,
    ]),

    history: PropTypes.instanceOf(Map),

    form: PropTypes.string,

    formValues: PropTypes.shape({
      id: PropTypes.any,
      dataSource: PropTypes.array, // type|pin|columnType, e.g. dataStream|100|Start Time
      sources: PropTypes.arrayOf(PropTypes.shape({
        sourceType: PropTypes.oneOf(['RAW_DATA', 'SUM', 'AVG', 'MED', 'MIN', 'MAX', 'COUNT']),
        dataStream: PropTypes.shape({
          id: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
          pin: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
          name: PropTypes.string
        }),
        selectedColumns: PropTypes.array,
      })),
      fineControlEnabled: PropTypes.bool
    }),

    dataStreams: PropTypes.arrayOf(PropTypes.shape({
      id: PropTypes.number,
      values: PropTypes.shape({
        pin: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
        label: PropTypes.string,
      })
    })),

    initializeForm: PropTypes.func,

    deviceId: PropTypes.number,

    initialValues: PropTypes.object,

    params: PropTypes.any,
  };

  constructor(props) {
    super(props);

    this.handleSave = this.handleSave.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.colorPickerComponent = this.colorPickerComponent.bind(this);
    this.sourceMultipleSelectComponent = this.sourceMultipleSelectComponent.bind(this);
  }

  componentWillUpdate(nextProps) {
    if(!nextProps.visible && this.props.visible !== nextProps.visible) {
      this.props.resetForm('bar-chart-widget-preview');
    }

    if (!_.isEqual(nextProps.initialValues, this.props.initialValues)) {
      this.props.initializeForm(nextProps.form, nextProps.initialValues);
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

  colorPickerComponent({input}) {
    return (
      <ColorPicker colors={this.colorPalette}
                   title="primary color" color={input.value}
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
          return parseInt(stream.pin) === parseInt(pin);
        });
      };

      let dataStream = getStreamByPin(value);

      if(!dataStream)
        return props.changeForm(this.props.form, 'sources.0.dataStream', null);

      props.changeForm(this.props.form, 'sources.0.dataStream', dataStream);

      let min = Number(dataStream && dataStream.min);
      let max = Number(dataStream && dataStream.max);

      if(!isNaN(min) && !isNaN(max) && min !== max) {
        props.changeForm(this.props.form, 'minValue', min);
        props.changeForm(this.props.form, 'maxValue', max);
      }

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

  valuePositionComponent(props) {
    return (
      <Radio.Group onChange={props.input.onChange} value={props.input.value} className={`modal-window-widget-settings-config--switch-text-alignment`}>
        <Radio value={WIDGETS_SLIDER_VALUE_POSITION.LEFT}>
          Left
        </Radio>
        <Radio value={WIDGETS_SLIDER_VALUE_POSITION.RIGHT}>
          Right
        </Radio>
      </Radio.Group>
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

  sliderNameComponent({input}) {
    return (
      <SimpleContentEditable maxLength={35}
                             className="modal-window-widget-settings-config-widget-name"
                             value={input.value}
                             onChange={input.onChange}/>
    );
  }

  handleCancel() {
    if (typeof this.props.onClose === 'function')
      this.props.onClose();

    this.props.resetForm(this.props.form);
  }

  handleSave() {

    if (typeof this.props.handleSubmit === 'function')
      this.props.handleSubmit();
  }

  getSourceOptions() {
    const dataStreamsOptions = [];

    this.props.dataStreams.forEach((stream) => {

      dataStreamsOptions.push({
        key: `${stream.pin}`,
        value: `${stream.label} (V${stream.pin})`,
      });


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

    return {
      'Data Streams': dataStreamsOptions
    };
  }

  render() {

    const sourcesOptions = this.getSourceOptions();

    return (
      <WidgetSettings
        onSave={this.handleSave}
        onCancel={this.handleCancel}
        visible={this.props.visible}
        preview={(
          <Preview
            deviceId={this.props.deviceId}
            data={this.props.formValues}
            history={this.props.history}
            loading={this.props.loading}/>
        )}
        config={(
          <div>
            <div className="modal-window-widget-settings-config-column-header">
              <Field name="label" component={this.sliderNameComponent}/>

              <div className="modal-window-widget-settings-config-column-bar-configuration">

                  <ItemsGroup>
                    <Item label="Data" offset="medium">
                      <Select displayError={false}
                              dropdownMatchSelectWidth={false}
                              name="sources.0.sourceType"
                              values={WIDGETS_SOURCE_TYPES_LIST}
                              placeholder="Choose type"
                              filterOption={this.simpleMatch}
                              validate={[Validation.Rules.required]}
                              style={{width: '100px'}}/>
                    </Item>
                    <Item label=" " offset="medium" style={{width: '100%'}}>

                      <Field name="sources.0.dataStream"
                             style={{width: '100%'}}
                             placeholder="Choose Source"
                             validate={[Validation.Rules.required]}
                             component={this.sourceMultipleSelectComponent}
                             values={sourcesOptions}
                             dataStreams={this.props.dataStreams}
                             formValues={this.props.formValues}
                             changeForm={this.props.changeForm}
                             form={this.props.form}
                             filterOption={this.simpleMatch}
                             notFoundContent={sourcesOptions["Data Streams"].length > 0 ?
                               "No DataStreams to match search" : "Create at least one DataStream"}
                      />

                    </Item>
                  </ItemsGroup>

                <Row>
                  <Col span={8}>
                    <Item label="min" offset="medium" displayError={false}>
                      <FormField name={'minValue'} placeholder={`0`} validate={[Validation.Rules.required, Validation.Rules.number]}/>
                    </Item>
                  </Col>
                  <Col span={8} offset={1}>
                    <Item label="max" offset="medium" displayError={false}>
                      <FormField name={'maxValue'} placeholder={`100`} validate={[Validation.Rules.required, Validation.Rules.number]}/>
                    </Item>
                  </Col>
                </Row>

                <Item offset={'normal'}>
                  <Field name={'sendOnReleaseOn'} component={this.switchComponent} label={'Send values on release only (optimal)'}/>
                </Item>

                <Item label="Color" offset="medium" className="widgets--label-widget--settings-background-color-picker">
                  <Field name="color" component={this.colorPickerComponent} />
                </Item>

                <h3 className="widgets--switch-widget--settings-h3" style={{'marginTop': '44px'}}>Steps</h3>

                <Row>
                  <Col span={8}>
                    <Item label="step" offset="normal" displayError={false}>
                      <FormField name={'step'} placeholder={`1`} validate={[Validation.Rules.required, Validation.Rules.number]}/>
                    </Item>
                  </Col>
                </Row>

                <Item offset={'normal'}>
                  <Field name={'fineControlEnabled'} component={this.switchComponent} label={'Show fine controls'}/>
                </Item>

                { this.props.formValues.fineControlEnabled === true && (

                  <Row>
                    <Col span={8}>
                      <Item label="fine control step" offset="medium" displayError={false}>
                        <FormField name={'fineControlStep'} placeholder={`1`} validate={[Validation.Rules.required, Validation.Rules.number]}/>
                      </Item>
                    </Col>
                  </Row>

                )}

                <h3 className="widgets--switch-widget--settings-h3" style={{'marginTop': '28px'}}>Value</h3>

                <Item label="Value position" offset="normal">
                  <Field name={'valuePosition'}
                         component={this.valuePositionComponent}
                  />
                </Item>

                <Row>
                  <Col span={8}>
                    <Item label="Decimals" offset="medium">
                      <FormField name={'decimalFormat'} placeholder={'Example: #.#'}/>
                    </Item>
                  </Col>
                  <Col span={8} offset={1}>
                    <Item label="Suffix" offset="medium">
                      <FormField name={'valueSuffix'} placeholder={'Example: %'}/>
                    </Item>
                  </Col>
                </Row>

              </div>
            </div>
          </div>
        )}/>
    );
  }

}

export default SliderWidgetSettings;
