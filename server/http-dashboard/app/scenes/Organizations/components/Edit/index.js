import React                from 'react';
import {MainLayout}         from 'components';
import Manage               from './../Manage';
import {Button}             from 'antd';
import PropTypes            from 'prop-types';
import {List, Map}          from 'immutable';
import {reduxForm}          from 'redux-form';

@reduxForm({
  validate: (data) => {

    const errors = {};

    if (!data.admins || !data.admins.length)
      errors['admins'] = 'Should have at least one administrator';

    return errors;
  }
})
class Edit extends React.Component {

  static propTypes = {
    activeTab: PropTypes.string,
    products: PropTypes.instanceOf(List),

    onCancel: PropTypes.func,
    onTabChange: PropTypes.func,
    handleCancel: PropTypes.func,
    handleSubmit: PropTypes.func,

    submitting: PropTypes.bool,
    submitFailed: PropTypes.bool,

    formErrors: PropTypes.instanceOf(Map)
  };

  render() {
    return (
      <MainLayout>
        <MainLayout.Header title="Edit Organization"
                           options={(
                             <div>
                               <Button type="default"
                                       onClick={this.props.onCancel}>
                                 Cancel
                               </Button>
                               <Button type="primary"
                                       loading={this.props.submitting}
                                       disabled={this.props.formErrors.size && this.props.submitFailed}
                                       onClick={this.props.handleSubmit}>
                                 Save
                               </Button>
                             </div>
                           )}/>
        <MainLayout.Content className="organizations-create-content">
          <Manage
            edit={this.props.edit} // hardcoded to hide admins tab
            submitFailed={this.props.submitFailed}
            formErrors={this.props.formErrors}
            onTabChange={this.props.onTabChange}
            activeTab={this.props.activeTab}
            products={this.props.products}/>
        </MainLayout.Content>
      </MainLayout>
    );
  }

}

export default Edit;
