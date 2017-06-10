import React from 'react';
import Base from '../Base';
import {Fieldset} from 'components';
import UnitModal from './modal';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {reset, getFormValues, getFormSyncErrors} from 'redux-form';
import {Unit as Units} from 'services/Products';

@connect((state, ownProps) => ({
  values: getFormValues(ownProps.form)(state),
  errors: getFormSyncErrors(ownProps.form)(state)
}), (dispatch) => ({
  resetForm: bindActionCreators(reset, dispatch)
}))
class Unit extends Base {

  constructor(props) {
    super(props);
  }

  getPreviewComponent() {

    const field = this.props.data;

    return (
      <Fieldset>
        <Fieldset.Legend type="dark">{field.get('name')}</Fieldset.Legend>
        { !field.get('value') && !field.get('units') ? <i>No Value</i> : (
          <div>
            {field.get('value')} {Units[field.get('units')].abbreviation}
          </div>
        ) }
      </Fieldset>
    );
  }

  getEditableComponent() {
    return (
      <div>
        <UnitModal form={this.props.form} initialValues={this.props.data.toJS()}/>
      </div>
    );
  }

}

export default Unit;
