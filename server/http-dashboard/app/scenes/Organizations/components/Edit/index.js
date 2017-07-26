import React                from 'react';
import {MainLayout}         from 'components';
import Manage               from './../Manage';
import {Button}             from 'antd';
import PropTypes            from 'prop-types';
import {List, Map}          from 'immutable';
import {reduxForm}          from 'redux-form';

@reduxForm()
class Edit extends React.Component {

  static propTypes = {
    activeTab: PropTypes.string,

    products: PropTypes.instanceOf(List),

    onCancel: PropTypes.func,
    onTabChange: PropTypes.func,
    handleCancel: PropTypes.func,
    handleSubmit: PropTypes.func,

    adminsComponent: PropTypes.element,
    productsComponent: PropTypes.element,

    submitting: PropTypes.bool,
    submitFailed: PropTypes.bool,

    formErrors: PropTypes.instanceOf(Map),
    formValues: PropTypes.instanceOf(Map)
  };

  render() {
    return (
      <MainLayout>
        <MainLayout.Header title={this.props.formValues.get('name')}
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
            submitFailed={this.props.submitFailed}
            formValues={this.props.formValues}
            formErrors={this.props.formErrors}
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
