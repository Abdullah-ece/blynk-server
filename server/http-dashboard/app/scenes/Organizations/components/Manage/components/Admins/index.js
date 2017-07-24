import React                from 'react';
import {AdminInviteForm}    from './components';
import {Roles}              from 'services/Roles';
import {connect}            from 'react-redux';
import {
  fromJS,
  Map
}                           from 'immutable';
import {Manage}             from 'services/Organizations';
import {bindActionCreators} from 'redux';
import {
  change,
  getFormValues,
  SubmissionError,
  reset
}                           from 'redux-form';
import PropTypes            from 'prop-types';
import './styles.less';

@connect((state) => ({
  formValues: fromJS(getFormValues(Manage.FORM_NAME)(state)),
}), (dispatch) => ({
  resetForm: bindActionCreators(reset, dispatch),
  changeForm: bindActionCreators(change, dispatch),
}))
class Admins extends React.Component {

  static propTypes = {
    resetForm: PropTypes.func,
    changeForm: PropTypes.func,

    formValues: PropTypes.instanceOf(Map),
  };

  constructor(props) {
    super(props);

    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleSubmitSuccess = this.handleSubmitSuccess.bind(this);
  }

  handleSubmitSuccess() {
    this.props.resetForm(Manage.ADMIN_INVITE_FORM_NAME);
  }

  handleSubmit(data) {

    const alreadyExists = this.props.formValues.get('admins').some((admin) => admin.email === data.email);

    if (alreadyExists) {
      throw new SubmissionError({
        'email': 'Email already exists'
      });
    }

    this.props.changeForm(Manage.FORM_NAME, 'admins', this.props.formValues.get('admins').update((admins) => admins.push({
      name: data.name,
      email: data.email,
      role: Roles.ADMIN.value
    })));
  }

  render() {
    return (
      <div className="organizations">
        <div>Add at least one Administrator. Invitations will be sent out once you save the Organization.</div>
        <AdminInviteForm onSubmit={this.handleSubmit} onSubmitSuccess={this.handleSubmitSuccess}/>
        <div>
          admins table there
        </div>
      </div>
    );
  }

}

export default Admins;
