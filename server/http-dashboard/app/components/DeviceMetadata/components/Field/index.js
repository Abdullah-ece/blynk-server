import React from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {reset, initialize, getFormValues, getFormSyncErrors, reduxForm} from 'redux-form';
import _ from 'lodash';

@reduxForm()
@connect((state, ownProps) => ({
  values: getFormValues(ownProps.form)(state),
  errors: getFormSyncErrors(ownProps.form)(state)
}), (dispatch) => ({
  resetForm: bindActionCreators(reset, dispatch),
  initialize: bindActionCreators(initialize, dispatch)
}))
class Field extends React.Component {

  static propTypes = {
    form: React.PropTypes.string,
    initialize: React.PropTypes.func,
    initialValues: React.PropTypes.object,
    resetForm: React.PropTypes.func,
    values: React.PropTypes.object,
    errors: React.PropTypes.object,
    children: React.PropTypes.object,
  };

  componentWillUpdate(nextProps) {
    if (!_.isEqual(nextProps.initialValues, this.props.initialValues)) {
      this.props.initialize(this.props.form, nextProps.initialValues);
    }
  }

  render() {

    return React.cloneElement(this.props.children, {
      values: this.props.values,
      errors: this.props.errors,
      resetForm: this.props.resetForm,
      initialize: this.props.initialize
    });
  }

}

export default Field;
