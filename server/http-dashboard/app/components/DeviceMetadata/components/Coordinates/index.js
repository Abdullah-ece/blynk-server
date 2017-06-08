import React from 'react';
import Base from '../Base';
import {Fieldset} from 'components';
import CoordinatesModal from './modal';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {reset, getFormValues, getFormSyncErrors} from 'redux-form';

@connect((state, ownProps) => ({
  values: getFormValues(ownProps.form)(state),
  errors: getFormSyncErrors(ownProps.form)(state)
}), (dispatch) => ({
  resetForm: bindActionCreators(reset, dispatch)
}))
class Coordinates extends Base {

  constructor(props) {
    super(props);
  }

  getPreviewComponent() {

    const field = this.props.data;

    return (
      <Fieldset>
        <Fieldset.Legend type="dark">{field.get('name')}</Fieldset.Legend>
        {field.get('lat')}, {field.get('lon')}
      </Fieldset>
    );
  }

  getEditableComponent() {
    return (
      <div>
        <CoordinatesModal form={this.props.form} initialValues={this.props.data.toJS()}/>
      </div>
    );
  }

}

export default Coordinates;
