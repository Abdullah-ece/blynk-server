import React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import {
  Button,
  Form
} from 'antd';
import { Manage } from 'services/Organizations';
import Validation from 'services/Validation';
import {
  destroy,
  reduxForm,
} from 'redux-form';
import { Input, Item } from 'components/UI';
import PropTypes from 'prop-types';
import './styles.less';

@connect(() => ({}), (dispatch) => ({
  destroyForm: bindActionCreators(destroy, dispatch)
}))
@reduxForm({
  form: 'organization-create-admin-invite-form',
  touchOnBlur: false
})
class AdminInviteForm extends React.Component {

  static propTypes = {
    destroyForm: PropTypes.func,
    handleSubmit: PropTypes.func,

    addText: PropTypes.string,

    loading: PropTypes.bool,
  };

  componentWillUnmount() {
    this.props.destroyForm(Manage.ADMIN_INVITE_FORM_NAME);
  }

  render() {
    return (
      <Form layout="inline" className="admin-invite-form">
        <Item label="admin name" offset="normal">
          <Input name="name" placeholder="Admin Name"
                 validate={[Validation.Rules.required,
                   Validation.Rules.minLength(3),
                   Validation.Rules.max(255)]}/>
        </Item>
        <Item label="Email address" offset="normal">
          <Input name="email" placeholder="Email address"
                 validateOnBlur={true}
                 validate={[
                   Validation.Rules.required,
                   Validation.Rules.email,
                   Validation.Rules.max(255)
                 ]}/>
        </Item>
        <Item position="center">
          <Button type="primary"
                  size="default"
                  className="admin-invite-form--button"
                  loading={this.props.loading}
                  onClick={this.props.handleSubmit}>{this.props.addText || 'Add'}</Button>
        </Item>
      </Form>
    );
  }

}

export default AdminInviteForm;
