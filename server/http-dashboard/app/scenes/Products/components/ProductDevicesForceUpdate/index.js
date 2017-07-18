import React            from 'react';
import {Modal}          from 'components';
import {Button, Radio}  from 'antd';
import _                from 'lodash';
import {
  DEVICE_FORCE_UPDATE
}                       from 'services/Products';
import {
  getOptionByAmount
}                       from 'services/Text';
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

  shouldComponentUpdate(nextProps, nextState) {
    if (this.props.isModalVisible !== nextProps.isModalVisible)
      return true;

    if (!_.isEqual(this.props.product, nextProps.product))
      return true;

    if (this.props.loading !== nextProps.loading)
      return true;

    if (this.state.option !== nextState.option)
      return true;

    return false;
  }

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

    const DevicesText = getOptionByAmount(this.props.product.deviceCount, ['Device', 'Devices']);

    return (
      <Modal title="Apply Changes?"
             visible={this.props.isModalVisible}
             confirmLoading={this.props.loading}
             onCancel={this.props.onCancel}
             closable={false}
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
            { getOptionByAmount(this.props.product.deviceCount, ['There is', ['There are']])}
            {'\u00A0'}
            <span>{ this.props.product.deviceCount }</span> active {DevicesText} associated with product:
            {'\u00A0'}
            <span>{this.props.product.name}</span>
          </div>
          <div className="product-device-force-update-option">
            <div className="product-device-force-update-option-title">
              How to apply changes?
            </div>
            <div className="product-device-force-update-option-options">
              <Radio.Group value={this.state.option} onChange={this.handleSelectOption.bind(this)}>
                <Radio value={DEVICE_FORCE_UPDATE.UPDATE_DEVICES}>
                  Update {this.props.product.deviceCount} active { DevicesText }
                </Radio>
                <Radio value={DEVICE_FORCE_UPDATE.SAVE_WITHOUT_UPDATE}>
                  Save changes. Don't update active { DevicesText }
                </Radio>
                <Radio value={DEVICE_FORCE_UPDATE.CLONE_PRODUCT}>
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
