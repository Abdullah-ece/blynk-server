import React           from 'react';
import {Modal}         from 'components';
import {Button, Radio} from 'antd';
import {
  DEVICE_FORCE_UPDATE
}                      from 'services/Products';
import './styles.less';

class ProductDeviceForceUpdate extends React.Component {

  static propTypes = {
    isModalVisible: React.PropTypes.bool,
    loading: React.PropTypes.bool,
    onSave: React.PropTypes.func,
    onCancel: React.PropTypes.func,
    product: React.PropTypes.object,
  };

  state = {
    option: DEVICE_FORCE_UPDATE.SAVE_WITHOUT_UPDATE
  };

  handleSelectOption(event) {
    this.setState({
      option: event.target.value
    });
  }

  handleSave() {
    if (this.props.onSave) {
      this.props.onSave(this.state.option);
    }
  }

  render() {
    return (
      <Modal title="Apply Changes?"
             visible={this.props.isModalVisible}
             confirmLoading={this.props.loading}
             onCancel={this.props.onCancel}
             footer={[
               <Button key="save" type="primary" size="default"
                       loading={this.props.loading}
                       onClick={this.handleSave.bind(this)}>
                 Continue
               </Button>,
               <Button key="cancel" type="default" size="default"
                       onClick={this.props.onCancel}>Cancel</Button>
             ]}
      >
        <div className="product-device-force-update">
          <div className="product-device-force-update-title">
            There are <span>{ this.props.product.deviceCount }</span> active Devices associated with product:
            <span>{this.props.product.name}</span>
          </div>
          <div className="product-device-force-update-option">
            <div className="product-device-force-update-option-title">
              How to apply changes?
            </div>
            <div className="product-device-force-update-option-options">
              <Radio.Group value={this.state.option} onChange={this.handleSelectOption.bind(this)}>
                <Radio value={DEVICE_FORCE_UPDATE.UPDATE_DEVICES} disabled={true}>
                  Update {this.props.product.deviceCount} active Devices
                </Radio>
                <Radio value={DEVICE_FORCE_UPDATE.SAVE_WITHOUT_UPDATE}>
                  Save changes. Don't update active Devices
                </Radio>
                <Radio value={DEVICE_FORCE_UPDATE.CLONE_PRODUCT} disabled={true}>
                  Create a clone of this Product with updated Metadata
                </Radio>
              </Radio.Group>
            </div>
          </div>
        </div>
      </Modal>
    );
  }

}

export default ProductDeviceForceUpdate;
