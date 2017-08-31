import React from 'react';
import {
  Modal,
  SimpleContentEditable
} from 'components';
import Source from './source';
import {Row, Col} from 'antd';
import {reduxForm, Field, getFormValues} from 'redux-form';
import {connect} from 'react-redux';
import {fromJS, Map} from 'immutable';
import PropTypes from 'prop-types';

// import {bindActionCreators} from 'redux';

@connect((state, ownProps) => ({
  formValues: fromJS(getFormValues(ownProps.form)(state) || {})
}))
@reduxForm({
  initialValues: {
    "type": "WEB_GRAPH",
    "id": 0,
    "x": 3,
    "y": 4,
    "color": 0,
    "width": 20,
    "height": 10,
    "label": "graph",
    "dataStreams": [{
      "pin": 1,
      "pwmMode": false,
      "rangeMappingOn": false,
      "pinType": "VIRTUAL",
      "min": 0,
      "max": 255
    }]
  }
})
class LinearWidgetSettings extends React.Component {

  static propTypes = {
    formValues: PropTypes.instanceOf(Map)
  };

  constructor(props) {
    super(props);

    this.onChange = this.onChange.bind(this);
    this.onChange2 = this.onChange2.bind(this);
    this.handleClick = this.handleClick.bind(this);
  }

  state = {
    value: 'Chart Title',
    value2: 'Temperature',
  };

  onChange(value) {
    this.setState({
      value: value
    });
  }

  onChange2(value) {
    this.setState({
      value2: value
    });
  }

  handleClick() {
    this.setState({
      value: (Math.random()).toString()
    });
  }

  labelNameComponent({input}) {
    return (
      <SimpleContentEditable className="modal-window-widget-settings-config-widget-name"
                             value={input.value}
                             onChange={input.onChange}/>
    );
  }

  render() {

    return (
      <Modal width={'auto'}
             wrapClassName="modal-window-widget-settings"
             visible={true}
             closable={false}
             okText={'Save'}
             cancelText={'Close'}>
        <Row type="flex">
          <Col span={12} className="modal-window-widget-settings-config-column">
            <div className="modal-window-widget-settings-config-column-header">
              <Field name="label" component={this.labelNameComponent}/>

              {/*<div className="modal-window-widget-settings-config-add-source">*/}
                {/*<Button type="dashed" onClick={this.handleClick}>Add source</Button>*/}
              {/*</div>*/}

            </div>
            <div className="modal-window-widget-settings-config-column-sources">
              <Source form="source1"/>
            </div>
          </Col>
          <Col span={12} className="modal-window-widget-settings-preview-column">
            Awesome
          </Col>
        </Row>
      </Modal>
    );
  }

}

export default LinearWidgetSettings;
