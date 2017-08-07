import React                from 'react';
import {MainLayout}         from 'components';
import Manage               from './../Manage';
import {Button}             from 'antd';
import PropTypes            from 'prop-types';
import {List, Map}          from 'immutable';
import {reduxForm}          from 'redux-form';
import {
  Admins
}                           from 'scenes/Organizations/components/Manage/components';
import {ProductsCreate}     from 'scenes/Organizations';

import './styles.less';

@reduxForm({
  validate: (data) => {

    const errors = {};

    if (!data.admins || !data.admins.length)
      errors['admins'] = 'Should have at least one administrator';

    return errors;
  }
})
class Create extends React.Component {

  static propTypes = {
    activeTab: PropTypes.string,
    products: PropTypes.instanceOf(List),

    onCancel: PropTypes.func,
    onTabChange: PropTypes.func,
    handleCancel: PropTypes.func,
    handleSubmit: PropTypes.func,

    submitting: PropTypes.bool,
    submitFailed: PropTypes.bool,

    formErrors: PropTypes.instanceOf(Map),
    formAsyncErrors: PropTypes.instanceOf(Map),
    formSubmitErrors: PropTypes.instanceOf(Map),
    formValues: PropTypes.instanceOf(Map),
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
                                       disabled={this.props.formSubmitErrors.size && this.props.formAsyncErrors.size && this.props.formErrors.size && this.props.submitFailed}
                                       onClick={this.props.handleSubmit}>
                                 Create
                               </Button>
                             </div>
                           )}/>
        <MainLayout.Content className="organizations-create-content">
          <Manage
            formValues={this.props.formValues}
            submitFailed={this.props.submitFailed}
            formErrors={this.props.formErrors}
            formAsyncErrors={this.props.formAsyncErrors}
            formSubmitErrors={this.props.formSubmitErrors}
            onTabChange={this.props.onTabChange}
            activeTab={this.props.activeTab}
            adminsComponent={<Admins submitFailed={this.props.submitFailed}/>}
            productsComponent={<ProductsCreate products={this.props.products}/>}
            products={this.props.products}/>
        </MainLayout.Content>
      </MainLayout>
    );
  }

}

export default Create;
