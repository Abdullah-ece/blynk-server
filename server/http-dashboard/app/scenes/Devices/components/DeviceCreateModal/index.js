import React from 'react';
import _ from 'lodash';
import {Modal} from 'components';
import {Row, Col, Button} from 'antd';
import {Item, Input} from 'components/UI';
import {MetadataSelect} from 'components/Form';
import Validation from 'services/Validation';
import {reduxForm} from 'redux-form';
import {AVAILABLE_HARDWARE_TYPES, AVAILABLE_CONNECTION_TYPES, SETUP_PRODUCT_KEY} from 'services/Devices';
import './styles.less';
import PropTypes from 'prop-types';

@reduxForm({
  form         : "DeviceCreate"
})
class DeviceCreateModal extends React.Component {

  static propTypes = {
    visible: PropTypes.bool,
    loading: PropTypes.bool,

    errors      : PropTypes.object,
    organization: PropTypes.object,
    account     : PropTypes.object,
    formValues  : PropTypes.object,
    router      : PropTypes.object,

    organizations: PropTypes.array,
    products     : PropTypes.array,

    handleSubmit    : PropTypes.func,
    onClose         : PropTypes.func,
    change          : PropTypes.func,
    onProductSelect : PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleClose = this.handleClose.bind(this);
    this.handleProductSelect = this.handleProductSelect.bind(this);
  }


  handleClose() {
    if(typeof this.props.onClose === 'function')
      this.props.onClose();
  }

  handleProductSelect(productId) {
    this.props.onProductSelect(productId);
  }

  render() {

    let organizations = [];

    let products = [];

    if (this.props.organizations) {
      organizations = [...this.props.organizations];

      organizations.push(this.props.organization);

      organizations = organizations.map((org) => ({
        key  : String(org.id),
        value: org.name
      }));
    }

    if (this.props.formValues && this.props.account.selectedOrgId && this.props.organizations) {
      if (Number(this.props.account.selectedOrgId) === Number(this.props.organization.id)) {

        products = this.props.products.map((product) => ({
          key  : String(product.id),
          value: product.name
        }));

      } else if (this.props.formValues) {

        let selectedOrgIndex = _.findIndex(this.props.organizations, (org) => (
          Number(org.id) === Number(this.props.account.selectedOrgId)
        ));

        if (selectedOrgIndex && this.props.organizations[selectedOrgIndex] && this.props.organizations[selectedOrgIndex].products) {
          products = this.props.organizations[selectedOrgIndex].products.map((product) => ({
            key  : String(product.id),
            value: product.name
          }));
        }

      }
    }

    // if (this.props.formValues && this.props.organization.parentId === -1 && Number(this.props.organization.id) === Number(this.props.account.selectedOrgId)) {
    //   products.unshift({
    //     key  : SETUP_PRODUCT_KEY,
    //     value: 'New product'
    //   });
    // }

    const isAdvancedOptionShouldBeDisplayed = this.props.formValues && this.props.formValues.productId === SETUP_PRODUCT_KEY;

    return (
      <Modal title="New Device"
             visible={this.props.visible}
             confirmLoading={this.props.loading}
             closable={false}
             onCancel={this.handleCancelClick}
             footer={[
               <Button key="cancel" type="default" size="default"
                       onClick={this.handleClose}>Cancel</Button>,
               <Button key="save" type="primary" size="default" disabled={(!!this.props.errors && !this.props.loading) || !this.props.products.length}
                       loading={this.props.loading}
                       onClick={this.props.handleSubmit}>
                 Create
               </Button>,
             ]}>
        <div className="device-create-modal">
          <Row>
            <Col span={24}>
              <Item label="Organization" offset="large">
                <MetadataSelect displayError={false} name="orgId" values={organizations}
                                placeholder="Choose organization"
                                validate={[Validation.Rules.required]}/>
              </Item>
            </Col>
          </Row>
          {this.props.formValues && this.props.account.selectedOrgId && (
            <Row>
              <Col span={24}>
                <Item label="Product Template" offset="large">
                  { products.length && (
                   <MetadataSelect onSelect={this.handleProductSelect} displayError={false} name="productId" values={products} placeholder="Choose product"
                                  validate={[Validation.Rules.required]}/>

                  ) || (
                    <div>This org has no products</div>
                  )}
                </Item>
              </Col>
            </Row>)
          }
          <Row>
            <Col span={24}>
              <Item label="Device Name" offset={isAdvancedOptionShouldBeDisplayed ? 'large' : 'none'}>
                <Input name="name" placeholder="New Device" validate={[Validation.Rules.required]}/>
              </Item>
            </Col>
          </Row>
          {isAdvancedOptionShouldBeDisplayed && (
            <Row>
              <Col span={10}>
                <Item label="Hardware" offset="none">
                  <MetadataSelect displayError={false} name="boardType" values={AVAILABLE_HARDWARE_TYPES}
                                  placeholder="Choose hardware type"
                                  validate={[Validation.Rules.required]}/>
                </Item>
              </Col>
              <Col span={12} offset={2}>
                <Item label="Connection Type" offset="none">
                  <MetadataSelect displayError={false} name="connectionType" values={AVAILABLE_CONNECTION_TYPES}
                                  placeholder="Choose connection type"
                                  validate={[Validation.Rules.required]}/>
                </Item>
              </Col>
            </Row>
          )}
        </div>
      </Modal>
    );
  }

}

export default DeviceCreateModal;
