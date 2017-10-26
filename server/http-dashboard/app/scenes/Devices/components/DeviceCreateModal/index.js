import React from 'react';
import _ from 'lodash';
import {Modal} from 'components';
import {Row, Col, Button} from 'antd';
import {Item, Input} from 'components/UI';
import {MetadataSelect} from 'components/Form';
import {connect} from 'react-redux';
import {Map, fromJS} from 'immutable';
import {bindActionCreators} from 'redux';
import Validation from 'services/Validation';
import {reduxForm, getFormSyncErrors, getFormValues, reset, change} from 'redux-form';
import {
  DeviceCreate,
  DevicesFetch,
  DeviceAvailableOrganizationsFetch,
} from 'data/Devices/api';
import {
  DeviceCreateUpdate,
} from 'data/Devices/actions';
import {ProductCreate} from 'data/Product/api';
import {AVAILABLE_HARDWARE_TYPES, AVAILABLE_CONNECTION_TYPES, STATUS} from 'services/Devices';
import './styles.less';

@connect((state) => ({
  deviceCreate: state.Devices.get('deviceCreate'),
  account: state.Account,
  organization: state.Organization,
  products: state.Product.products,
  errors: getFormSyncErrors('DeviceCreate')(state),
  formValues: getFormValues('DeviceCreate')(state)
}), (dispatch) => ({
  change: bindActionCreators(change, dispatch),
  resetForm: bindActionCreators(reset, dispatch),
  fetchDevices: bindActionCreators(DevicesFetch, dispatch),
  createDevice: bindActionCreators(DeviceCreate, dispatch),
  createProduct: bindActionCreators(ProductCreate, dispatch),
  DeviceCreateUpdate: bindActionCreators(DeviceCreateUpdate, dispatch),
  DeviceAvailableOrganizationsFetch: bindActionCreators(DeviceAvailableOrganizationsFetch, dispatch),
}))
@reduxForm({
  form: "DeviceCreate",
  initialValues: {
    productId: null
  }
})
class DeviceCreateModal extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    deviceCreate: React.PropTypes.instanceOf(Map),
    visible: React.PropTypes.bool,
    onClose: React.PropTypes.func,
    errors: React.PropTypes.object,
    organization: React.PropTypes.object,
    account: React.PropTypes.object,
    formValues: React.PropTypes.object,
    products: React.PropTypes.array,
    reduxForm: React.PropTypes.func,
    resetForm: React.PropTypes.func,
    createDevice: React.PropTypes.func,
    createProduct: React.PropTypes.func,
    fetchDevices: React.PropTypes.func,
    change: React.PropTypes.func,
    DeviceCreateUpdate: React.PropTypes.func,
    DeviceAvailableOrganizationsFetch: React.PropTypes.func,
    router: React.PropTypes.object,
  };

  state = {
    loading: false,
    productId: null,
    previousBoardType: null,
    previousConnectionType: null
  };

  componentWillMount() {
    this.props.DeviceAvailableOrganizationsFetch().then();
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps && nextProps.formValues && nextProps.formValues.productId !== this.state.productId) {
      this.setState({
        productId: nextProps.formValues.productId
      });

      const product = _.find(this.props.products, (product) => {
        return Number(product.id) === Number(nextProps.formValues.productId);
      });

      if (nextProps.formValues.productId === this.SETUP_PRODUCT_KEY && this.props.formValues.productId !== this.SETUP_PRODUCT_KEY) {
        this.props.change('boardType', this.state.previousBoardType || null);
        this.props.change('connectionType', this.state.previousConnectionType || null);
      }

      if (nextProps.formValues.productId !== this.SETUP_PRODUCT_KEY && this.props.formValues.productId === this.SETUP_PRODUCT_KEY) {
        this.setState({
          previousBoardType: this.props.formValues && this.props.formValues.boardType || null,
          previousConnectionType: this.props.formValues && this.props.formValues.connectionType || null
        });
      }

      if (product) {
        this.props.change('boardType', product.boardType);
        this.props.change('connectionType', product.connectionType);
      }
    }

    if (this.props.formValues && this.props.formValues.orgId && Number(this.props.formValues.orgId) !== Number(nextProps.formValues.orgId)) {
      this.props.change('productId', '');
    }

    if (this.props.formValues && !nextProps.formValues.orgId) {
      this.props.change('orgId', this.props.organization.id);
    }
  }

  SETUP_PRODUCT_KEY = 'SETUP_NEW_PRODUCT';

  redirectToNewDevice(id) {
    this.props.resetForm('DeviceCreate');
    this.props.onClose();
    this.context.router.push(`/devices/${id}`);
  }

  handleCancelClick() {
    this.props.resetForm('DeviceCreate');
    this.props.onClose();
  }

  handleOkClick() {

    const createDevice = (productId) => {
      this.props.createDevice({
        orgId: this.props.formValues.orgId
      }, {
        ...this.props.formValues,
        productId: productId || this.props.formValues.productId,
        status: STATUS.OFFLINE
      }).then((response) => {
        this.props.fetchDevices({
          orgId: this.props.account.orgId
        }).then(() => {
          this.setState({
            loading: false
          });
          if(response.payload.data && response.payload.data.id) {
            this.redirectToNewDevice(response.payload.data.id);
          } else {
            this.handleCancelClick();
          }
        });
      });
    };

    this.setState({
      loading: true
    });

    if (this.props.formValues.productId === this.SETUP_PRODUCT_KEY) {
      this.props.createProduct({
        orgId: this.props.organization.id,
        product: {
          "name": `New Product ${_.random(1,999999999)}`,
          "boardType": this.props.formValues.boardType,
          "connectionType": this.props.formValues.connectionType,
        }
      }).then((response) => {
        createDevice(response.payload.data.id);
      });
    } else {
      createDevice();
    }
  }

  render() {

    let organizations = [];

    let products = [];

    if (this.props.deviceCreate.get('data')) {
      organizations = this.props.deviceCreate.get('data').push(fromJS(this.props.organization)).map((org) => ({
        key: String(org.get('id')),
        value: org.get('name')
      })).toJS();
    }

    if (this.props.formValues && this.props.formValues.orgId && this.props.deviceCreate.get('data')) {
      if (Number(this.props.formValues.orgId) === Number(this.props.organization.id)) {

        products = this.props.products.map((product) => ({
          key: String(product.id),
          value: product.name
        }));

      } else if (this.props.formValues) {

        let index = this.props.deviceCreate.get('data').findIndex((org) => Number(org.get('id')) === Number(this.props.formValues.orgId));
        products = this.props.deviceCreate.getIn(['data', index, 'products']).map((product) => ({
          key: String(product.get('id')),
          value: product.get('name')
        })).toJS();

      }
    }

    if (this.props.formValues && this.props.organization.parentId === -1 && Number(this.props.organization.id) === Number(this.props.formValues.orgId)) {
      products.unshift({
        key: this.SETUP_PRODUCT_KEY,
        value: 'New product'
      });
    }

    const isAdvancedOptionShouldBeDisplayed = this.props.formValues && this.props.formValues.productId === this.SETUP_PRODUCT_KEY;

    return (
      <Modal title="New Device"
             visible={this.props.visible}
             confirmLoading={this.state.loading}
             closable={false}
             onCancel={this.handleCancelClick.bind(this)}
             footer={[
               <Button key="cancel" type="default" size="default"
                       onClick={this.handleCancelClick.bind(this)}>Cancel</Button>,
               <Button key="save" type="primary" size="default" disabled={!!this.props.errors}
                       loading={this.state.loading}
                       onClick={this.handleOkClick.bind(this)}>
                 Create
               </Button>,
             ]}>
        <div className="device-create-modal">
          <Row>
            <Col span={24}>
              <Item label="Device Name" offset="large">
                <Input name="name" placeholder="New Device" validate={[Validation.Rules.required]}/>
              </Item>
            </Col>
          </Row>
          <Row>
            <Col span={24}>
              <Item label="Assign to Organization"
                    offset={this.props.formValues && this.props.formValues.orgId ? 'large' : 'none'}>
                <MetadataSelect displayError={false} name="orgId" values={organizations}
                                placeholder="Choose organization"
                                validate={[Validation.Rules.required]}/>
              </Item>
            </Col>
          </Row>
          {this.props.formValues && this.props.formValues.orgId && (
            <Row>
              <Col span={24}>
                <Item label="Product Template" offset={isAdvancedOptionShouldBeDisplayed ? 'large' : 'none'}>
                  <MetadataSelect displayError={false} name="productId" values={products} placeholder="Choose product"
                                  validate={[Validation.Rules.required]}/>
                </Item>
              </Col>
            </Row>)
          }
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
