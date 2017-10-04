import React          from 'react';
import WidgetSettings from '../WidgetSettings';
import PropTypes      from 'prop-types';

import Validation     from 'services/Validation';
import ColorPicker    from 'components/ColorPicker';

import {
  BAR_CHART_PARAMS
} from 'services/Widgets';

import {
  Row,
  Col,
  Select as AntdSelect
} from 'antd';

import {
  Item,
  ItemsGroup
} from "components/UI";

import {
  MetadataSelect as Select
} from 'components/Form';

import {
  reduxForm,
  Field,
  reset,
} from 'redux-form';

import {
  SimpleContentEditable
} from 'components';

import {
  connect
} from 'react-redux';
import {
  bindActionCreators
} from 'redux';

@connect((/*state*/) => ({}), (dispatch) => ({
  resetForm: bindActionCreators(reset, dispatch)
}))
@reduxForm()
class BarChartSettings extends React.Component {

  static propTypes = {
    visible: PropTypes.bool,
    pristine: PropTypes.bool,

    onClose: PropTypes.func,
    onSave: PropTypes.func,
    handleSubmit: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleSave = this.handleSave.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
  }

  labelNameComponent({input}) {
    return (
      <SimpleContentEditable maxLength={35}
                             className="modal-window-widget-settings-config-widget-name"
                             value={input.value} o
                             onChange={input.onChange}/>
    );
  }

  colorPickerComponent({input}) {
    return (
      <ColorPicker title="primary color" color={input.value}
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

  multipleTagsSelect(props) {
    return (
      <AntdSelect mode="multiple"
                  onFocus={props.input.onFocus}
                  onBlur={props.input.onBlur}
                  onChange={props.input.onChange}
                  value={props.input.value || []}
                  style={{width: '100%'}}
                  placeholder="Group By"
                  allowClear={true}>
        { Object.keys(BAR_CHART_PARAMS.GROUP_BY.list).map((key) => (
            <AntdSelect.OptGroup key={key}>
              {
                BAR_CHART_PARAMS.GROUP_BY.list[key].map((item) => (
                  <AntdSelect.Option value={item.key} key={item.key}>
                    {item.value}
                  </AntdSelect.Option>
                ))
              }
            </AntdSelect.OptGroup>
        )) }
      </AntdSelect>
    );
  }

  render() {

    return (
      <WidgetSettings
        visible={this.props.visible}
        onSave={this.handleSave}
        onCancel={this.handleCancel}
        isSaveDisabled={this.props.pristine}
        config={(
          <div>
            <div className="modal-window-widget-settings-config-column-header">
              <Field name="label" component={this.labelNameComponent}/>

              <div className="modal-window-widget-settings-config-add-source">
                {/*<Button type="dashed" onClick={this.handleAddSource}>Add source</Button>*/}
              </div>

            </div>

            <div className="modal-window-widget-settings-config-column-bar-configuration">
              <ItemsGroup>
                <Item label="X: Data" offset="large">
                  <Select displayError={false}
                          dropdownMatchSelectWidth={false}
                          name="dataType"
                          values={BAR_CHART_PARAMS.DATA_TYPE.list}
                          placeholder="Choose type"
                          validate={[Validation.Rules.required]}
                          style={{width: '100px'}}/>
                </Item>
                <Item label=" " offset="large">
                  <Select displayError={false}
                          dropdownMatchSelectWidth={false}
                          values={BAR_CHART_PARAMS.DATA_SOURCE.list}
                          name="dataSource"
                          placeholder="Choose Source"
                          validate={[Validation.Rules.required]}
                          style={{width: '100%'}}/>
                </Item>
              </ItemsGroup>
              <Item label="Y: Group By" offset="large">

                <Field name="groupDataBy"
                       component={this.multipleTagsSelect}/>
              </Item>
              <ItemsGroup>
                <Item label="Sort By" offset="large">
                  <Select displayError={false}
                          dropdownMatchSelectWidth={false}
                          name="sortBy"
                          values={BAR_CHART_PARAMS.SORT_BY.list}
                          placeholder="Choose type"
                          validate={[Validation.Rules.required]}
                          style={{width: '100%'}}
                  />
                </Item>
                <Item label=" ">
                  <Select displayError={false}
                          dropdownMatchSelectWidth={false}
                          values={BAR_CHART_PARAMS.SORT_BY_ORDER.list}
                          name="sortType"
                          placeholder="Choose Source"
                          validate={[Validation.Rules.required]}
                          style={{width: '100px'}}
                  />
                </Item>
              </ItemsGroup>
              <Row>
                <Col span={8}>

                  <Item label="Max rows">
                    <Select displayError={false}
                            dropdownMatchSelectWidth={false}
                            values={BAR_CHART_PARAMS.MAX_ROWS.list}
                            name="maxRows"
                            placeholder="Choose Max Rows"
                            validate={[Validation.Rules.required]}
                            style={{width: '100px'}}
                    />
                  </Item>

                </Col>
                <Col span={12}>

                  <Item label="Color">
                    <Field component={this.colorPickerComponent} name="color"/>
                  </Item>

                </Col>
              </Row>
            </div>
          </div>
        )}

        preview={(
          <div>Preview</div>
        )}
      />
    );
  }

}

export default BarChartSettings;
