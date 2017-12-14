import React from 'react';

import WidgetSettings from '../WidgetSettings';
import PropTypes from 'prop-types';
import {reduxForm, getFormValues, reset, initialize, destroy, change, Field} from 'redux-form';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {fromJS} from 'immutable';
import _ from 'lodash';
import {
  WIDGETS_SOURCE_TYPES_LIST,
  WIDGETS_LABEL_DATA_FORMATS,
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
} from 'antd';
import {
  SimpleContentEditable
} from 'components';
import {
  Item,
  ItemsGroup
} from "components/UI";

@connect((state, ownProps) => ({
  formValues: (getFormValues(ownProps.form)(state) || {}),
  dataStreams: (state.Product.edit.dataStreams && state.Product.edit.dataStreams.fields || []),
}), (dispatch) => ({
  changeForm: bindActionCreators(change, dispatch),
  resetForm: bindActionCreators(reset, dispatch),
  initializeForm: bindActionCreators(initialize, dispatch),
  destroyForm: bindActionCreators(destroy, dispatch),
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
          pin: PropTypes.number,
          name: PropTypes.string
        }),
        selectedColumns: PropTypes.array,
        dataFormat: PropTypes.oneOf([WIDGETS_LABEL_DATA_FORMATS.NUMBER, WIDGETS_LABEL_DATA_FORMATS.STRING])
      })),
    }),

    dataStreams: PropTypes.arrayOf(PropTypes.shape({
      id: PropTypes.number,
      values: PropTypes.shape({
        pin: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
        label: PropTypes.string,
      })
    }))
  };

  constructor(props) {
    super(props);

    this.handleSave = this.handleSave.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.sourceMultipleSelectComponent = this.sourceMultipleSelectComponent.bind(this);
  }

  sourceMultipleSelectComponent(props) {

    const onChange = (value) => {

      const getStreamByPin = (pin) => {
        return _.find(props.dataStreams, (stream) => {
          return parseInt(stream.values.pin) === parseInt(pin);
        });
      };

      const getColumnByLabel = (label = null, columns = []) => {
        return _.find(columns, (column) => String(column.label) === String(label));
      };

      let dataStream = null;
      let selectedColumns = [];

      value.forEach((source) => {

        let [pin, columnLabel] = source.split('|');

        if(!dataStream) {
          dataStream = getStreamByPin(pin);
        }

        if(columnLabel) {

          let column = getColumnByLabel(columnLabel, dataStream.values.tableDescriptor.columns);

          selectedColumns.push({
            name: column.columnName,
            label: column.label,
            type: 'COLUMN',
          });

        }

      });

      if(!dataStream || !dataStream.values) {
        props.input.onChange({});
        props.changeForm(this.props.form, 'sources.0.selectedColumns', []);
        return null;
      }

      props.input.onChange(
        fromJS(dataStream.values).set('tableDescriptor', null).toJS()
      );

      props.changeForm(this.props.form, 'sources.0.selectedColumns', selectedColumns);

    };

    const getValue = () => {

      if(!props.input.value) return [];

      let values = [];

      let pin = this.props.formValues.sources[0].dataStream.pin;

      if(isNaN(Number(pin)))
        return [];

      (this.props.formValues.sources[0].selectedColumns || []).forEach((column) => {
        values.push(`${pin}|${column.label}`);
      });

      if(!values.length) // display stream as selected only if no columns selected
        values.push(String(pin));

      return values;
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
      <AntdSelect mode="multiple"
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

  incomingDataComponent(props) {
    return (
      <Radio.Group onChange={props.input.onChange} value={props.input.value}>
        <Radio value={WIDGETS_LABEL_DATA_FORMATS.NUMBER}>Number</Radio>
        <Radio value={WIDGETS_LABEL_DATA_FORMATS.STRING}>String</Radio>
      </Radio.Group>
    );
  }

  labelNameComponent({input}) {
    return (
      <SimpleContentEditable maxLength={35}
                             className="modal-window-widget-settings-config-widget-name"
                             value={input.value} o
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
        key: `${stream.values.pin}`,
        value: stream.values.label,
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
          <div>Label Widget Preview</div>
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
                             "No DataStreams or Fields match search" : "Create at least one DataStream with PIN >= 100"}
                    />

                  </Item>
                </ItemsGroup>
                <Item label="Incoming Data Format" offset="normal">
                  <Field name={'sources.0.dataFormat'}
                         component={this.incomingDataComponent}
                  />
                </Item>

                { this.props.formValues.sources[0].dataFormat === WIDGETS_LABEL_DATA_FORMATS.NUMBER && (

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

              </div>
            </div>
          </div>
        )}/>
    );
  }

}

export default LabelWidgetSettings;
