import React from 'react';
import {Button, Tabs, Icon, Popover} from 'antd';
import MetadataIntroductionMessage from "../MetadataIntroductionMessage";
import InfoTab from 'scenes/Products/components/ProductManage/components/Info';
import MetadataTab from 'scenes/Products/components/ProductManage/components/Metadata';
import ProductHeader from 'scenes/Products/components/ProductHeader';
import ProductContent from 'scenes/Products/components/ProductContent';

class ProductEdit extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    onTabChange: React.PropTypes.func,
    handleCancel: React.PropTypes.func,
    handleSubmit: React.PropTypes.func,
    onInfoValuesChange: React.PropTypes.func,
    onMetadataFieldChange: React.PropTypes.func,
    onMetadataFieldsChange: React.PropTypes.func,
    updateMetadataFirstTimeFlag: React.PropTypes.func,

    isMetadataInfoRead: React.PropTypes.bool,
    isInfoFormInvalid: React.PropTypes.bool,
    isMetadataFormInvalid: React.PropTypes.bool,

    params: React.PropTypes.object,
    product: React.PropTypes.object
  };

  constructor(props) {
    super(props);

    this.state = {
      originalName: null,
      submited: false,
      activeTab: props && props.params.tab || this.TABS.INFO,
      metadataIntroVisible: false
    };
  }

  componentWillMount() {
    if (!this.state.originalName) {
      this.setState({
        originalName: this.props.product.info.values.name
      });
    }
  }

  TABS = {
    INFO: 'info',
    METADATA: 'metadata',
    // DATA_STREAMS: 'datastreams',
    // EVENTS: 'events'
  };

  isMetadataIntroductionMessageVisible() {
    if (!this.props.isMetadataInfoRead) return true;

    return this.state.metadataIntroVisible;
  }


  toggleMetadataIntroductionMessage() {

    this.setState({
      metadataIntroVisible: !this.state.metadataIntroVisible,
    });

    if (!this.props.isMetadataInfoRead) {
      this.props.updateMetadataFirstTimeFlag(false);
      this.setState({
        metadataIntroVisible: false
      });
    }
  }

  handleTabChange(key) {
    this.setState({
      activeTab: key
    });

    this.context.router.push(`/products/edit/${this.props.params.id}/${key}`);

    this.props.onTabChange(key);
  }

  isInfoFormInvalid() {
    return this.props.isInfoFormInvalid;
  }

  productInfoInvalidIcon() {
    return this.state.submited && this.isInfoFormInvalid() &&
      <Icon type="exclamation-circle-o" className="product-tab-invalid"/> || null;
  }

  productMetadataInvalidIcon() {
    return this.state.submited && this.props.isMetadataFormInvalid &&
      <Icon type="exclamation-circle-o" className="product-tab-invalid"/> || null;
  }

  handleSubmit() {

    this.setState({
      submited: true
    });

    this.props.handleSubmit();
  }

  render() {

    return (
      <div className="products-create">
        <ProductHeader title={this.state.originalName}
                       options={(
                         <div>
                           <Button type="default"
                                   onClick={this.props.handleCancel.bind(this)}>
                             Cancel
                           </Button>
                           <Button type="primary"
                                   onClick={this.handleSubmit.bind(this)}
                                   disabled={this.state.submited && (this.props.isInfoFormInvalid || this.props.isMetadataFormInvalid)}>
                             Save
                           </Button>
                         </div>
                       )}/>
        <ProductContent>
          { this.state.activeTab === this.TABS.METADATA && <Popover
            placement="bottomRight"
            content={<MetadataIntroductionMessage onGotItClick={this.toggleMetadataIntroductionMessage.bind(this)}/>}
            visible={this.isMetadataIntroductionMessageVisible()}
            overlayClassName="products-metadata-introduction-message-popover"
            trigger="click">

            <Icon type="info-circle" className="products-metadata-info"
                  onClick={this.toggleMetadataIntroductionMessage.bind(this)}/>
          </Popover>}

          <Tabs defaultActiveKey={this.TABS.INFO}
                activeKey={this.state.activeTab}
                onChange={this.handleTabChange.bind(this)} className="products-tabs">

            <Tabs.TabPane tab={<span>{this.productInfoInvalidIcon()}Info</span>} key={this.TABS.INFO}>
              <InfoTab values={this.props.product.info.values}
                       onChange={this.props.onInfoValuesChange}/>
            </Tabs.TabPane>

            <Tabs.TabPane tab={<span>{this.productMetadataInvalidIcon()}Metadata</span>} key={this.TABS.METADATA}>
              <MetadataTab fields={this.props.product.metadata.fields}
                           onFieldChange={this.props.onMetadataFieldChange}
                           onFieldsChange={this.props.onMetadataFieldsChange}/>
            </Tabs.TabPane>

          </Tabs>

        </ProductContent>
      </div>
    );
  }
}

export default ProductEdit;
