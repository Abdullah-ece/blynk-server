import React from 'react';
import {Button, Tabs, Icon, Popover} from 'antd';
import MetadataIntroductionMessage from "../MetadataIntroductionMessage";
import InfoTab from 'scenes/Products/components/ProductManage/components/Info';
import MetadataTab from 'scenes/Products/components/ProductManage/components/Metadata';
import DataStreamsTab from 'scenes/Products/components/ProductManage/components/DataStreams';
import EventsTab from 'scenes/Products/components/ProductManage/components/Events';
import ProductHeader from 'scenes/Products/components/ProductHeader';
import ProductContent from 'scenes/Products/components/ProductContent';
import {TABS} from 'services/Products';

class ProductCreate extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    handleCancel: React.PropTypes.func,
    handleSubmit: React.PropTypes.func,
    onInfoValuesChange: React.PropTypes.func,
    onEventsFieldsChange: React.PropTypes.func,
    onMetadataFieldChange: React.PropTypes.func,
    onMetadataFieldsChange: React.PropTypes.func,
    onDataStreamsFieldChange: React.PropTypes.func,
    onDataStreamsFieldsChange: React.PropTypes.func,
    updateMetadataFirstTimeFlag: React.PropTypes.func,

    isMetadataInfoRead: React.PropTypes.bool,
    isInfoFormInvalid: React.PropTypes.bool,
    isEventsFormInvalid: React.PropTypes.bool,
    isMetadataFormInvalid: React.PropTypes.bool,
    isDataStreamsFormInvalid: React.PropTypes.bool,

    params: React.PropTypes.object,
    product: React.PropTypes.object
  };

  constructor(props) {
    super(props);

    this.state = {
      originalName: null,
      submited: false,
      activeTab: props.params.tab || TABS.INFO,
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
  }

  isInfoFormInvalid() {
    return this.props.isInfoFormInvalid;
  }

  productInfoInvalidIcon() {
    return this.state.submited && this.isInfoFormInvalid() &&
      <Icon type="exclamation-circle-o" className="product-tab-invalid"/> || null;
  }

  productDataStreamsInvalidIcon() {
    return this.state.submited && this.props.isDataStreamsFormInvalid &&
      <Icon type="exclamation-circle-o" className="product-tab-invalid"/> || null;
  }

  productMetadataInvalidIcon() {
    return this.state.submited && this.props.isMetadataFormInvalid &&
      <Icon type="exclamation-circle-o" className="product-tab-invalid"/> || null;
  }

  productEventsInvalidIcon() {
    return this.state.submited && this.props.isEventsFormInvalid &&
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
        <ProductHeader title="New Product"
                       options={(
                         <div>
                           <Button type="default"
                                   onClick={this.props.handleCancel.bind(this)}>
                             Cancel
                           </Button>
                           <Button type="primary"
                                   onClick={this.handleSubmit.bind(this)}
                                   disabled={this.state.submited && (this.props.isDataStreamsFormInvalid || this.props.isInfoFormInvalid || this.props.isMetadataFormInvalid)}>
                             Create
                           </Button>
                         </div>
                       )}/>
        <ProductContent>
          { this.state.activeTab === TABS.METADATA && <Popover
            placement="bottomRight"
            content={<MetadataIntroductionMessage onGotItClick={this.toggleMetadataIntroductionMessage.bind(this)}/>}
            visible={this.isMetadataIntroductionMessageVisible()}
            overlayClassName="products-metadata-introduction-message-popover"
            trigger="click">

            <Icon type="info-circle" className="products-metadata-info"
                  onClick={this.toggleMetadataIntroductionMessage.bind(this)}/>
          </Popover>}

          <Tabs defaultActiveKey={TABS.INFO}
                activeKey={this.state.activeTab}
                onChange={this.handleTabChange.bind(this)} className="products-tabs">

            <Tabs.TabPane tab={<span>{this.productInfoInvalidIcon()}Info</span>} key={TABS.INFO}>
              <InfoTab values={this.props.product.info.values}
                       onChange={this.props.onInfoValuesChange}/>
            </Tabs.TabPane>

            <Tabs.TabPane tab={<span>{this.productMetadataInvalidIcon()}Metadata</span>} key={TABS.METADATA}>
              <MetadataTab fields={this.props.product.metadata.fields}
                           onFieldChange={this.props.onMetadataFieldChange}
                           onEventsChange={this.props.onEventsFieldsChange}
                           onFieldsChange={this.props.onMetadataFieldsChange}/>
            </Tabs.TabPane>

            <Tabs.TabPane tab={<span>{this.productDataStreamsInvalidIcon()}Data Streams</span>} key={TABS.DATA_STREAMS}>
              <DataStreamsTab fields={this.props.product.dataStreams.fields}
                              onFieldChange={this.props.onDataStreamsFieldChange}
                              onFieldsChange={this.props.onDataStreamsFieldsChange}/>
            </Tabs.TabPane>

            <Tabs.TabPane tab={<span>{this.productEventsInvalidIcon()}Events</span>} key={TABS.EVENTS}>
              <EventsTab fields={this.props.product.events.fields}
                         onFieldsChange={this.props.onEventsFieldsChange}/*
               onFieldsChange={this.props.onDataStreamsFieldsChange}*//>
            </Tabs.TabPane>

          </Tabs>

        </ProductContent>
      </div>
    );
  }
}

export default ProductCreate;
