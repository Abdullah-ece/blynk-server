import React from 'react';
import Base from '../Base';
import {Fieldset} from 'components';
import TextModal from './modal';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {reset, getFormValues} from 'redux-form';

@connect((state, ownProps) => ({
  values: getFormValues(ownProps.form)(state)
}), (dispatch) => ({
  resetForm: bindActionCreators(reset, dispatch)
}))
class Text extends Base {

  constructor(props) {
    super(props);
  }

  getPreviewComponent() {

    const field = this.props.data;

    return (
      <Fieldset>
        <Fieldset.Legend type="dark">{field.get('name')}</Fieldset.Legend>
        {field.get('value')}
      </Fieldset>
    );
  }

  getEditableComponent() {
    return (
      <div>
        <TextModal form={this.props.form} initialValues={this.props.data.toJS()}/>
      </div>
    );
  }

}

export default Text;
