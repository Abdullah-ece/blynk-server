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
import MetadataIntroductionMessage          from '../MetadataIntroductionMessage';

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

    isFormDirty: React.PropTypes.bool,
    isMetadataInfoRead: React.PropTypes.bool,
    isInfoFormInvalid: React.PropTypes.bool,
    isEventsFormInvalid: React.PropTypes.bool,
    isMetadataFormInvalid: React.PropTypes.bool,
    isDataStreamsFormInvalid: React.PropTypes.bool,

    params: React.PropTypes.object,
    product: React.PropTypes.object,

    successButtonLabel: React.PropTypes.string
  };

  constructor(props) {
    super(props);

    this.state = {
      originalName: null,
      submited: false,
      activeTab: props && props.params.tab || TABS.INFO,
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

  render() {

    return (
      <MainLayout>
        <MainLayout.Header title={this.state.originalName}
                           options={(
                             <div>
                               <Button type="default"
                                       onClick={this.props.handleCancel.bind(this)}>
                                 Cancel
                               </Button>
                               <Button type="primary"
                                       onClick={this.handleSubmit.bind(this)}
                                       disabled={this.props.isFormDirty === false || (this.state.submited && (this.props.isDataStreamsFormInvalid || this.props.isInfoFormInvalid || this.props.isMetadataFormInvalid))}>
                                 { this.props.successButtonLabel || 'Save' }
                               </Button>
                             </div>
                           )}/>
        <MainLayout.Content>
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
                         onFieldsChange={this.props.onEventsFieldsChange}/>
            </Tabs.TabPane>

          </Tabs>

        </MainLayout.Content>
      </MainLayout>
    );
  }
}

export default ProductEdit;
