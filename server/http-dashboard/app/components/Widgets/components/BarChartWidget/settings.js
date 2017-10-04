import React          from 'react';
import WidgetSettings from '../WidgetSettings';
import PropTypes      from 'prop-types';

import Validation     from 'services/Validation';
import ColorPicker    from 'components/ColorPicker';

import {
  Row,
  Col
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
                          name="x"
                          values={[]}
                          placeholder="Choose type"
                          validate={[Validation.Rules.required]}
                          style={{width: '100px'}}/>
                </Item>
                <Item label=" " offset="large">
                  <Select displayError={false}
                          dropdownMatchSelectWidth={false}
                          values={[{key: 'Raw', value: 'Raw data'}]}
                          name="x2"
                          placeholder="Choose Source"
                          validate={[Validation.Rules.required]}
                          style={{width: '100%'}}/>
                </Item>
              </ItemsGroup>
              <Item label="Y: Group By" offset="large">
                <Select mode="tags"
                        displayError={false}
                        dropdownMatchSelectWidth={false}
                        name="ygroupBy"
                        values={[]}
                        placeholder="Choose type"
                        validate={[Validation.Rules.required]}
                        style={{width: '100%'}}/>
              </Item>
              <ItemsGroup>
                <Item label="Sort By" offset="large">
                  <Select displayError={false}
                          dropdownMatchSelectWidth={false}
                          name="sortby"
                          values={[]}
                          placeholder="Choose type"
                          validate={[Validation.Rules.required]}
                          style={{width: '100%'}}
                  />
                </Item>
                <Item label=" ">
                  <Select displayError={false}
                          dropdownMatchSelectWidth={false}
                          values={[{key: 'Raw', value: 'Raw data'}]}
                          name="sortbytype"
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
                            values={[{key: 'Raw', value: 'Raw data'}]}
                            name="maxrows"
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
