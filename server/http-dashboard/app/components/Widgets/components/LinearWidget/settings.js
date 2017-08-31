import React from 'react';
import {
  Modal,
  SimpleContentEditable
} from 'components';
import Source from './source';
import _ from 'lodash';
import {Row, Col} from 'antd';
import {reduxForm, Field, getFormValues, change, reset, destroy, initialize} from 'redux-form';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {fromJS, Map} from 'immutable';
import PropTypes from 'prop-types';

// import {bindActionCreators} from 'redux';

@connect((state, ownProps) => ({
  formValues: fromJS(getFormValues(ownProps.form)(state) || {})
}), (dispatch) => ({
  changeForm: bindActionCreators(change, dispatch),
  resetForm: bindActionCreators(reset, dispatch),
  initializeForm: bindActionCreators(initialize, dispatch),
  destroyForm: bindActionCreators(destroy, dispatch),
}))
@reduxForm()
class LinearWidgetSettings extends React.Component {

  static propTypes = {
    formValues: PropTypes.instanceOf(Map),
    visible: PropTypes.bool,

    initialValues: PropTypes.object,

    form: PropTypes.string,

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
    this.handleSourceChange = this.handleSourceChange.bind(this);
    this.handleClick = this.handleClick.bind(this);
  }

  state = {
    value: 'Chart Title',
    value2: 'Temperature',
  };

  componentWillUpdate(nextProps) {

    if (!_.isEqual(nextProps.initialValues, this.props.initialValues)) {
      this.props.initializeForm(nextProps.form, nextProps.initialValues);
      this.props.resetForm(nextProps.form, nextProps.initialValues);
    }
  }

  getSourceFormName(id) {
    return `${this.props.form}-source${id}`;
  }

  handleCancel() {
    this.props.onClose();

    this.props.resetForm(this.props.form);

    this.props.formValues.get('sources').forEach((source) => {
      this.props.resetForm(this.getSourceFormName(source.get('id')));
    });
  }

  handleSave() {
    this.props.handleSubmit();
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

  handleSourceChange(source) {

    const sources = this.props.formValues.get('sources').map((s) => {
      if (s.get('id') === source.id)
        return fromJS(source);
      return s;
    });

    this.props.changeForm(this.props.form, 'sources', sources);
  }

  render() {

    return (
      <Modal width={'auto'}
             wrapClassName="modal-window-widget-settings"
             visible={this.props.visible}
             closable={false}
             onCancel={this.handleCancel}
             onOk={this.handleSave}
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

              {this.props.formValues.has('sources') && this.props.formValues.get('sources').map((source, key) => (
                <Source form={this.getSourceFormName(source.get('id'))}
                        initialValues={source.toJS()} key={key}
                        onChange={this.handleSourceChange}/>
              ))}

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
