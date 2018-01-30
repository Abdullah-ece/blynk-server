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
  WIDGETS_SOURCE_TYPES_LIST,
  WIDGETS_LABEL_DATA_TYPES,
  WIDGETS_LABEL_TEXT_ALIGNMENT,
  WIDGETS_LABEL_LEVEL_POSITION,
} from 'services/Widgets';
import Validation from 'services/Validation';
import {
  Field as FormField,
  MetadataSelect as Select
} from 'components/Form';
import {
  Select as AntdSelect,
  Radio,
  Row,
  Col,
  Switch,
  Button,
} from 'antd';
import {
  ColorPicker,
  TextColorPicker,
  SimpleContentEditable,
  FontAwesome,
} from 'components';
import {
  Item,
  ItemsGroup
} from "components/UI";

import {
  WidgetDevicesPreviewHistoryClear
} from 'data/Widgets/actions';

import {FORMS} from 'services/Products';

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
  clearWidgetDevicePreviewHistory: bindActionCreators(WidgetDevicesPreviewHistoryClear, dispatch),
}))
@reduxForm()
class LabelWidgetSettings extends React.Component {

  static propTypes = {
    visible: PropTypes.bool,

    onClose: PropTypes.func,
    resetForm: PropTypes.func,
    changeForm: PropTypes.func,
    handleSubmit: PropTypes.func,

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
      dataType: PropTypes.oneOf([WIDGETS_LABEL_DATA_TYPES.NUMBER, WIDGETS_LABEL_DATA_TYPES.STRING]),
      isColorSetEnabled: PropTypes.bool,
      backgroundColor: PropTypes.any,
      textColor: PropTypes.any,
      isShowLevelEnabled: PropTypes.any,
      level: PropTypes.any,
      colorsSet: PropTypes.array,
    }),

    dataStreams: PropTypes.arrayOf(PropTypes.shape({
      id: PropTypes.number,
      values: PropTypes.shape({
        pin: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
        label: PropTypes.string,
      })
    })),

    clearWidgetDevicePreviewHistory: PropTypes.func,

    initializeForm: PropTypes.func,

    initialValues: PropTypes.object,

    params: PropTypes.any,
  };

  constructor(props) {
    super(props);

    this.handleSave = this.handleSave.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.handleAddColorSet = this.handleAddColorSet.bind(this);
    this.colorPickerComponent = this.colorPickerComponent.bind(this);
    this.handleRemoveColorSet = this.handleRemoveColorSet.bind(this);
    this.textColorPickerComponent = this.textColorPickerComponent.bind(this);
    this.sourceMultipleSelectComponent = this.sourceMultipleSelectComponent.bind(this);
  }

  componentWillUpdate(nextProps) {
    if(!nextProps.visible && this.props.visible !== nextProps.visible) {
      this.props.clearWidgetDevicePreviewHistory();
      this.props.resetForm('bar-chart-widget-preview');
    }

    if (!_.isEqual(nextProps.initialValues, this.props.initialValues)) {
      this.props.initializeForm(nextProps.form, nextProps.initialValues);
    }
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

  textColorPickerComponent({input, backgroundColor}) {
    return (
      <TextColorPicker colors={this.colorPalette}
                       title="primary color" backgroundColor={backgroundColor} color={input.value}
                   onChange={input.onChange}/>
    );
  }

  handleRemoveColorSet(key) {
    return () => {

      const clone = _.concat(this.props.formValues.colorsSet, []);

      _.remove(clone, (i, k) => {
        return Number(key) === Number(k);
      });

      this.props.changeForm(this.props.form, 'colorsSet', clone);
    };
  }

  handleAddColorSet() {

    const lastValue = _.last(this.props.formValues.colorsSet);

    const colorsSet = _.concat(this.props.formValues.colorsSet, [{
      min: null,
      max: null,
      backgroundColor: lastValue.backgroundColor,
      textColor: lastValue.textColor,
      customText: '',
    }]);

    this.props.changeForm(this.props.form, 'colorsSet', colorsSet);
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

  incomingDataComponent(props) {
    return (
      <Radio.Group onChange={props.input.onChange} value={props.input.value}>
        <Radio value={WIDGETS_LABEL_DATA_TYPES.NUMBER}>Number</Radio>
        <Radio value={WIDGETS_LABEL_DATA_TYPES.STRING}>String</Radio>
      </Radio.Group>
    );
  }

  levelPositionComponent(props) {
    return (
      <Radio.Group onChange={props.input.onChange} value={props.input.value}>
        <Radio value={WIDGETS_LABEL_LEVEL_POSITION.VERTICAL}>Vertical</Radio>
        <Radio value={WIDGETS_LABEL_LEVEL_POSITION.HORIZONTAL}>Horizontal</Radio>
      </Radio.Group>
    );
  }

  textAlignmentComponent(props) {
    return (
      <Radio.Group onChange={props.input.onChange} value={props.input.value}>
        <Radio.Button value={WIDGETS_LABEL_TEXT_ALIGNMENT.LEFT}>
          <FontAwesome name={'align-left'}/>
        </Radio.Button>
        <Radio.Button value={WIDGETS_LABEL_TEXT_ALIGNMENT.CENTER}>
          <FontAwesome name={'align-center'}/>
          {/*{ FormatAlignCenter }*/}
        </Radio.Button>
        <Radio.Button value={WIDGETS_LABEL_TEXT_ALIGNMENT.RIGHT}>
          <FontAwesome name={'align-right'}/>
          {/*{ FormatAlignRight }*/}
        </Radio.Button>
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

  labelNameComponent({input}) {
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
        value: stream.label,
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
            widgetId={this.props.formValues && Number(this.props.formValues.id || 0)}
            params={this.props.params}
            source={this.props.formValues && this.props.formValues.sources && this.props.formValues.sources[0] || {}}
            data={this.props.formValues}/>
        )}
        config={(
          <div>
            <div className="modal-window-widget-settings-config-column-header">
              <Field name="label" component={this.labelNameComponent}/>

              <div className="modal-window-widget-settings-config-add-source">
                {/*<Button type="dashed" onClick={this.handleAddSource}>Add source</Button>*/}
              </div>

              <div className="modal-window-widget-settings-config-column-bar-configuration">
                <ItemsGroup>
                  <Item label="X: Data" offset="medium">
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
                <Item label="Incoming Data Format" offset="normal">
                  <Field name={'dataType'}
                         component={this.incomingDataComponent}
                  />
                </Item>

                { this.props.formValues.dataType === WIDGETS_LABEL_DATA_TYPES.NUMBER && (

                  <Row>
                    <Col span={6}>
                      <Item label="Decimals" offset="medium">
                        <FormField name={'decimalFormat'} placeholder={'Example: #.#'}/>
                      </Item>
                    </Col>
                    <Col span={6} offset={1}>
                      <Item label="Suffix" offset="medium">
                        <FormField name={'valueSuffix'} placeholder={'Example: %'}/>
                      </Item>
                    </Col>
                  </Row>

                )}

                <Item label={'Text Alignment'} offset={'medium'}>
                  <Field name={'alignment'} component={this.textAlignmentComponent} />
                </Item>

                <div className="widgets--label-widget--settings-group-name">
                  Background
                </div>

                <Item offset={'normal'}>
                  <Field name={'isColorSetEnabled'} component={this.switchComponent} label={'Change color based on value'}/>
                </Item>

                { this.props.formValues.isColorSetEnabled === true && (

                  <div>

                    {this.props.formValues.colorsSet.map((item, key) => ((
                      <Row key={key} className="widgets--label-widget-background-colors-set">
                        <Col span={12}>
                          <ItemsGroup>
                            <Item label=" ">
                              <Field name={`colorsSet.${key}.backgroundColor`}
                                     component={this.colorPickerComponent}/>
                            </Item>
                            <Item label="min">
                              <FormField name={`colorsSet.${key}.min`} style={{width: '100px'}}/>
                            </Item>
                            <Item label="max">
                              <FormField name={`colorsSet.${key}.max`} style={{width: '100px'}}/>
                            </Item>
                          </ItemsGroup>
                        </Col>
                        <Col span={10} offset={1}>
                          <ItemsGroup>
                            <Item label=" ">
                              <Field name={`colorsSet.${key}.textColor`}
                                     component={this.textColorPickerComponent} backgroundColor={item.backgroundColor}/>
                            </Item>
                            <Item label="custom text (optional)">
                              <FormField name={`colorsSet.${key}.customText`}/>
                            </Item>
                          </ItemsGroup>
                        </Col>
                        <Col span={1}>
                          { this.props.formValues.colorsSet.length >= 2 && (
                            <Button type="danger" className="widgets--label-widget-background-colors-set--remove-button" onClick={this.handleRemoveColorSet(key)}>
                              <FontAwesome name={'trash'}/>
                            </Button>
                          )}
                        </Col>
                      </Row>
                    )))}

                    <Button type="dashed" className="danger widgets--label-widget-background-colors-set--add-button" onClick={this.handleAddColorSet}>
                      +
                    </Button>

                  </div>

                )}

                { this.props.formValues.isColorSetEnabled === false && (

                  <Row>
                    <Col span={6}>
                      <Item label="Background" offset="medium" className="widgets--label-widget--settings-background-color-picker">
                        <Field name="backgroundColor" component={this.colorPickerComponent} />
                      </Item>
                    </Col>
                    <Col span={6} offset={1}>
                      <Item label="Text" offset="medium" className="widgets--label-widget--settings-background-color-picker">
                        <Field name="textColor" component={this.textColorPickerComponent} backgroundColor={this.props.formValues.backgroundColor} />
                      </Item>
                    </Col>
                  </Row>

                )}

                <div className="widgets--label-widget--settings-group-name widgets--label-widget--settings-group-name--level">
                  Level
                </div>

                <Item offset={'normal'}>
                  <Field name={'isShowLevelEnabled'} component={this.switchComponent} label={'Show level'}/>
                </Item>

                { this.props.formValues.isShowLevelEnabled === true && (

                  <div>

                    <Row>
                      <Col span={6}>
                        <Item label="min value" offset="medium">
                          <FormField name={'level.min'}/>
                        </Item>
                      </Col>
                      <Col span={6} offset={1}>
                        <Item label="max value" offset="medium">
                          <FormField name={'level.max'}/>
                        </Item>
                      </Col>
                    </Row>

                    <Item label="Position" offset={'normal'}>
                      <Field name="level.position" component={this.levelPositionComponent} />
                    </Item>

                    <Item label="Color" offset={'normal'} className="widgets--label-widget--settings-background-color-picker">
                      <Field name="level.color" component={this.colorPickerComponent} />
                    </Item>

                  </div>


                )}

              </div>
            </div>
          </div>
        )}/>
    );
  }

}

export default LabelWidgetSettings;
