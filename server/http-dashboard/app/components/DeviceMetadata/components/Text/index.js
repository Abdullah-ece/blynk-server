import React from 'react';
import Base from '../Base';
import {Fieldset} from 'components';
import {reset} from 'redux-form';
import TextModal from './modal';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

@connect(() => ({}), (dispatch) => ({
  resetForm: bindActionCreators(reset, dispatch)
}))
class Text extends Base {

  constructor() {
    super();
  }

  getPreviewComponent() {

    const field = this.props.data;

    return (
      <Fieldset>
        <Fieldset.Legend type="dark">{field.name}</Fieldset.Legend>
        {field.value}
      </Fieldset>
    );
  }

  onCancel() {
    this.props.resetForm(this.getFormName());
  }

  getEditableComponent() {
    return (
      <div>
        <TextModal form={this.getFormName()} initialValues={this.props.data}/>
      </div>
    );
  }

}

export default Text;
