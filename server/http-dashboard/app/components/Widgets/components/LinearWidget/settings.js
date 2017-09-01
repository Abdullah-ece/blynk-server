import React from 'react';
import {
  Modal,
  SimpleContentEditable
} from 'components';
import Source from './source';
import {
  WIDGETS_PREDEFINED_SOURCE_OPTIONS
} from 'services/Widgets';
import _ from 'lodash';
import {Row, Col, Button} from 'antd';
import {reduxForm, Field, getFormValues, change, reset, destroy, initialize} from 'redux-form';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {fromJS, Map} from 'immutable';
import PropTypes from 'prop-types';

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
    pristine: PropTypes.bool,

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
    this.handleAddSource = this.handleAddSource.bind(this);
    this.handleSourceCopy = this.handleSourceCopy.bind(this);
    this.handleSourceDelete = this.handleSourceDelete.bind(this);
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

  handleSourceCopy(id) {
    const nextId = _.random(1, 999999999);

    const sourceIndex = this.props.formValues.get('sources').findIndex((source) => Number(source.get('id')) === Number(id));

    const source = this.props.formValues.getIn(['sources', sourceIndex]);

    const sources = this.props.formValues.get('sources').insert(sourceIndex + 1, fromJS({
      ...source.toJS(),
      id: nextId
    }));

    this.props.changeForm(this.props.form, 'sources', sources.toJS());
  }

  handleSourceDelete(id) {
    const sources = this.props.formValues.get('sources').update(sources => sources.filter((source) => Number(source.get('id')) !== Number(id)));

    this.props.changeForm(this.props.form, 'sources', sources.toJS());
  }

  handleCancel() {
    this.props.onClose();

    this.props.resetForm(this.props.form);
  }

  handleSave() {
    this.props.handleSubmit();
  }

  handleAddSource() {

    const id = _.random(1, 999999999);

    const sources = this.props.formValues.get('sources').push(fromJS({
      ...WIDGETS_PREDEFINED_SOURCE_OPTIONS,
      id: id
    }));

    this.props.changeForm(this.props.form, 'sources', sources.toJS());
  }

  labelNameComponent({input}) {
    return (
      <SimpleContentEditable className="modal-window-widget-settings-config-widget-name"
                             value={input.value} o
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
             footer={[
               <Button key="back" onClick={this.handleCancel}>
                 Close
               </Button>,
               <Button key="submit" type="primary" onClick={this.handleSave} disabled={this.props.pristine}>
                 Save
               </Button>,
             ]}>
        <Row type="flex">
          <Col span={12} className="modal-window-widget-settings-config-column">
            <div className="modal-window-widget-settings-config-column-header">
              <Field name="label" component={this.labelNameComponent}/>

              <div className="modal-window-widget-settings-config-add-source">
                <Button type="dashed" onClick={this.handleAddSource}>Add source</Button>
              </div>

            </div>
            <div className="modal-window-widget-settings-config-column-sources">

              {this.props.formValues.has('sources') && this.props.formValues.get('sources').map((source, key) => (
                <Source form={this.props.form}
                        index={key}
                        source={source} key={key}
                        onChange={this.handleSourceChange}
                        onDelete={this.handleSourceDelete}
                        onCopy={this.handleSourceCopy}
                />
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
