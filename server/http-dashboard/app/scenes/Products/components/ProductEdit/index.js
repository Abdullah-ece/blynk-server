import React                                from 'react';
import {
  Button,
  Tabs,
  Icon,
  Popover
}                                           from 'antd';
import {MainLayout}                         from 'components';
import {TABS}                               from 'services/Products';
import {
  Info        as InfoTab,
  Events      as EventsTab,
  Metadata    as MetadataTab,
  DataStreams as DataStreamsTab,
}                                           from '../ProductManage';

import _                        from 'lodash';
import DashboardTab                         from 'scenes/Products/scenes/Dashboard';
import MetadataIntroductionMessage          from '../MetadataIntroductionMessage';
import DeleteModal              from './components/Delete';

class ProductEdit extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    onTabChange: React.PropTypes.func,
    handleCancel: React.PropTypes.func,
    handleSubmit: React.PropTypes.func,
    onInfoValuesChange: React.PropTypes.func,
    onEventsFieldsChange: React.PropTypes.func,
    onMetadataFieldChange: React.PropTypes.func,
    onMetadataFieldsChange: React.PropTypes.func,
    onDataStreamsFieldChange: React.PropTypes.func,
    onDataStreamsFieldsChange: React.PropTypes.func,
    updateMetadataFirstTimeFlag: React.PropTypes.func,
    onDelete: React.PropTypes.func,

    isFormDirty: React.PropTypes.bool,
    isMetadataInfoRead: React.PropTypes.bool,
    isInfoFormInvalid: React.PropTypes.bool,
    isEventsFormInvalid: React.PropTypes.bool,
    isMetadataFormInvalid: React.PropTypes.bool,
    isDataStreamsFormInvalid: React.PropTypes.bool,

    params: React.PropTypes.object,
    product: React.PropTypes.object,

    loading: React.PropTypes.bool,
    successButtonLabel: React.PropTypes.string
  };

  constructor(props) {
    super(props);
    const currentProduct = _.find(this.props.product, {
      id: Number(this.props.params.id)
    });

    this.state = {
      originalName: null,
      submited: false,
      activeTab: props && props.params.tab || TABS.INFO,
      metadataIntroVisible: false,
      showDeleteModal: false,
      currentProduct: currentProduct,
    };

    this.toggleDelete = this.toggleDelete.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleTabChange = this.handleTabChange.bind(this);
    this.handleDeleteSubmit = this.handleDeleteSubmit.bind(this);
    this.toggleMetadataIntroductionMessage = this.toggleMetadataIntroductionMessage.bind(this);

  }

  componentWillMount() {
    if (!this.state.originalName) {
      this.setState({
        originalName: this.props.product.info.values.name
      });
    }
  }

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

    this.props.onTabChange(key);
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

  toggleDelete() {
    this.setState({
      showDeleteModal: !this.state.showDeleteModal
    });
  }

  handleDeleteSubmit() {
    return this.props.onDelete(this.props.params.id).then(() => {
      this.toggleDelete();
    });
  }

  render() {

    return (
      <div>
        <MainLayout.Header title={this.state.originalName}
                           options={(
                             <div>
                               <Button type="danger" onClick={this.toggleDelete}>Delete</Button>
                               <Button type="default"
                                       onClick={this.props.handleCancel}>
                                 Cancel
                               </Button>
                               <Button type="primary"
                                       onClick={this.handleSubmit}
                                       loading={this.props.loading}
                                       disabled={this.props.isFormDirty === false || (this.state.submited && (this.props.isDataStreamsFormInvalid || this.props.isInfoFormInvalid || this.props.isMetadataFormInvalid))}>
                                 { this.props.successButtonLabel || 'Save' }
                               </Button>
                             </div>
                           )}/>
        <MainLayout.Content className="product-edit-content">
          { this.state.activeTab === TABS.METADATA && <Popover
            placement="bottomRight"
            content={<MetadataIntroductionMessage onGotItClick={this.toggleMetadataIntroductionMessage}/>}
            visible={this.isMetadataIntroductionMessageVisible()}
            overlayClassName="products-metadata-introduction-message-popover"
            trigger="click">

            <Icon type="info-circle" className="products-metadata-info"
                  onClick={this.toggleMetadataIntroductionMessage}/>
          </Popover>}

          <Tabs defaultActiveKey={TABS.INFO}
                activeKey={this.state.activeTab}
                onChange={this.handleTabChange} className="products-tabs">

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
                         onFieldsChange={this.props.onEventsFieldsChange}/>
            </Tabs.TabPane>

            <Tabs.TabPane tab={<span>Dashboard</span>} key={TABS.DASHBOARD}>
              <DashboardTab params={this.props.params}/>
            </Tabs.TabPane>

          </Tabs>
          <DeleteModal deviceCount={this.state.currentProduct.deviceCount} onCancel={this.toggleDelete}
                       visible={this.state.showDeleteModal} handleSubmit={this.handleDeleteSubmit}
                       productName={this.state.currentProduct.name}/>
        </MainLayout.Content>
      </div>
    );
  }
}

export default ProductEdit;
