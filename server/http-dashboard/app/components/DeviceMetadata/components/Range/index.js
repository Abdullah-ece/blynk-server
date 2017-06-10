import React from 'react';
import Base from '../Base';
import {Fieldset} from 'components';
import RangeModal from './modal';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {TimeRange} from 'services/Metadata';
import {reset, initialize, getFormValues, getFormSyncErrors} from 'redux-form';

@connect((state, ownProps) => ({
  values: getFormValues(ownProps.form)(state),
  errors: getFormSyncErrors(ownProps.form)(state)
}), (dispatch) => ({
  resetForm: bindActionCreators(reset, dispatch),
  initialize: bindActionCreators(initialize, dispatch)
}))
class Range extends Base {

  constructor(props) {
    super(props);
  }

  getPreviewComponent() {

    const field = this.props.data;

    return (
      <Fieldset>
        <Fieldset.Legend type="dark">{field.get('name')}</Fieldset.Legend>
        from {TimeRange.fromMinutes(field.get('from'))} to {TimeRange.fromMinutes(field.get('to'))}
      </Fieldset>
    );
  }

  getEditableComponent() {
    return (
      <div>
        <RangeModal form={this.props.form} initialValues={this.props.data.toJS()}/>
      </div>
    );
  }

}

export default Range;
