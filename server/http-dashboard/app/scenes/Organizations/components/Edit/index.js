import React                from 'react';
import {MainLayout}         from 'components';
import Manage               from '../Manage';
import {Button, Popconfirm} from 'antd';
import PropTypes            from 'prop-types';
import {List, Map}          from 'immutable';
import {reduxForm}          from 'redux-form';
import { VerifyPermission, PERMISSIONS_INDEX } from "services/Roles";

@reduxForm()
class Edit extends React.Component {

  static propTypes = {
    activeTab: PropTypes.string,

    products: PropTypes.instanceOf(List),

    onCancel: PropTypes.func,
    onDelete: PropTypes.func,
    onTabChange: PropTypes.func,
    handleCancel: PropTypes.func,
    handleSubmit: PropTypes.func,
    permissions: PropTypes.number,

    adminsComponent: PropTypes.element,
    productsComponent: PropTypes.element,

    submitting: PropTypes.bool,
    submitFailed: PropTypes.bool,

    formErrors: PropTypes.instanceOf(Map),
    formAsyncErrors: PropTypes.instanceOf(Map),
    formSubmitErrors: PropTypes.instanceOf(Map),
    formValues: PropTypes.instanceOf(Map)
  };

  constructor(props) {
    super(props);

    this.handleCancel = this.handleCancel.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleCancel() {
    this.props.onCancel(this.props.activeTab);
  }

  handleSubmit() {
    this.props.handleSubmit();
  }

  render() {
    return (
      <MainLayout>
        {VerifyPermission(this.props.permissions, PERMISSIONS_INDEX.ORG_DELETE) && (<MainLayout.Header title={this.props.formValues.get('name')}
                           options={(
                             <div>
                               <Popconfirm title="Are you sure?" okText="Yes" cancelText="No"
                                           onConfirm={this.props.onDelete}>
                                 <Button type="danger">Delete</Button>
                               </Popconfirm>
                               <Button type="default"
                                       onClick={this.handleCancel}>
                                 Cancel
                               </Button>
                               <Button type="primary"
                                       loading={this.props.submitting}
                                       disabled={this.props.formSubmitErrors.size && this.props.formAsyncErrors.size && this.props.formErrors.size && this.props.submitFailed}
                                       onClick={this.handleSubmit}>
                                 Save
                               </Button>
                             </div>
                           )}/>)}
        <MainLayout.Content className="organizations-create-content">
          <Manage
            submitFailed={this.props.submitFailed}
            formValues={this.props.formValues}
            formErrors={this.props.formErrors}
            formAsyncErrors={this.props.formAsyncErrors}
            formSubmitErrors={this.props.formSubmitErrors}
            onTabChange={this.props.onTabChange}
            activeTab={this.props.activeTab}
            adminsComponent={this.props.adminsComponent}
            productsComponent={this.props.productsComponent}
            products={this.props.products}/>
        </MainLayout.Content>
      </MainLayout>
    );
  }

}

export default Edit;
