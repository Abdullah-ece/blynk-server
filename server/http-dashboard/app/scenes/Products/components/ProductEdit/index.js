import React                                from 'react';
import {
  Button,
  Tabs,
  Icon,
  // Popover
}                                           from 'antd';
import {MainLayout}                         from 'components';
import {TABS}                               from 'services/Products';
import {
  Info        as InfoTab,
  Events      as EventsTab,
  Metadata    as MetadataTab,
  DataStreams as DataStreamsTab,
  Dashboard   as DashboardTab
}                                           from '../ProductManage';

// import _                        from 'lodash';
// import MetadataIntroductionMessage          from '../MetadataIntroductionMessage';
// import DeleteModal              from './components/Delete';

import ProductDevicesForceUpdate from 'scenes/Products/components/ProductDevicesForceUpdate';

import {
  FieldArray,
  reduxForm,
} from 'redux-form';
import PropTypes from 'prop-types';
import {Map} from 'immutable';

@reduxForm()
class ProductEdit extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {

    initialValues: PropTypes.object,

    activeTab: PropTypes.string,

    formSyncErrors: PropTypes.instanceOf(Map),

    onTabChange: PropTypes.func,
    onCancel: PropTypes.func,
    onDevicesForceUpdateSubmit: PropTypes.func,
    onDevicesForceUpdateCancel: PropTypes.func,

    isDevicesForceUpdateVisible: PropTypes.bool,
    deviceForceUpdateLoading: PropTypes.bool,

    /*reduxForm props*/
    submitFailed: PropTypes.bool,
    submitting: PropTypes.bool,
    dirty: PropTypes.bool,
    invalid: PropTypes.bool,
    handleSubmit: PropTypes.func,
    /*end reduxForm props*/

    // onTabChange: React.PropTypes.func,
    // handleCancel: React.PropTypes.func,
    // handleSubmit: React.PropTypes.func,
    // onInfoValuesChange: React.PropTypes.func,
    // onEventsFieldsChange: React.PropTypes.func,
    // onMetadataFieldChange: React.PropTypes.func,
    // onMetadataFieldsChange: React.PropTypes.func,
    // onDataStreamsFieldChange: React.PropTypes.func,
    // onDataStreamsFieldsChange: React.PropTypes.func,
    // updateMetadataFirstTimeFlag: React.PropTypes.func,
    // onDelete: React.PropTypes.func,
    //
    // isFormDirty: React.PropTypes.bool,
    // isMetadataInfoRead: React.PropTypes.bool,
    // isInfoFormInvalid: React.PropTypes.bool,
    // isEventsFormInvalid: React.PropTypes.bool,
    // isMetadataFormInvalid: React.PropTypes.bool,
    // isDataStreamsFormInvalid: React.PropTypes.bool,
    //
    // params: React.PropTypes.object,
    // product: React.PropTypes.object,
    //
    // loading: React.PropTypes.bool,
    // successButtonLabel: React.PropTypes.string
  };

  constructor(props) {
    super(props);
    // const currentProduct = _.find(this.props.product, {
    //   id: Number(this.props.params.id)
    // });
    //
    this.state = {
      activeTab: props && props.activeTab || TABS.INFO,
      metadataIntroVisible: false,
      showDeleteModal: false,
    };
    //
    // this.toggleDelete = this.toggleDelete.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleTabChange = this.handleTabChange.bind(this);
    // this.handleDeleteSubmit = this.handleDeleteSubmit.bind(this);
    // this.toggleMetadataIntroductionMessage = this.toggleMetadataIntroductionMessage.bind(this);

  }

  componentWillMount() {
    // if (!this.state.originalName) {
    //   this.setState({
    //     originalName: this.props.product.info.values.name
    //   });
    // }
  }

  isMetadataIntroductionMessageVisible() {
    // if (!this.props.isMetadataInfoRead) return true;
    //
    // return this.state.metadataIntroVisible;
  }


  toggleMetadataIntroductionMessage() {

    // this.setState({
    //   metadataIntroVisible: !this.state.metadataIntroVisible,
    // });
    //
    // if (!this.props.isMetadataInfoRead) {
    //   this.props.updateMetadataFirstTimeFlag(false);
    //   this.setState({
    //     metadataIntroVisible: false
    //   });
    // }
  }

  handleTabChange(key) {
    this.props.onTabChange(key);
  }

  isInfoFormInvalid() {
    // return this.props.isInfoFormInvalid;
  }

  handleSubmit() {
    this.props.handleSubmit();
  }

  toggleDelete() {
    // this.setState({
    //   showDeleteModal: !this.state.showDeleteModal
    // });
  }

  handleDeleteSubmit() {
    // return this.props.onDelete(this.props.params.id).then(() => {
    //   this.toggleDelete();
    // });
  }

  productInfoInvalidIcon() {

    return this.props.submitFailed && (
      this.props.formSyncErrors.has('name') ||
      this.props.formSyncErrors.has('boardType') ||
      this.props.formSyncErrors.has('connectionType') ||
      this.props.formSyncErrors.has('description') ||
      this.props.formSyncErrors.has('logoUrl')
    ) && (<Icon type="exclamation-circle-o" className="product-tab-invalid"/>) || null;
  }


  productDataStreamsInvalidIcon() {
    const isAnyDataStreamHasError = () => {
      if(!this.props.formSyncErrors.has('dataStreams'))
        return false;

      return this.props.formSyncErrors.get('dataStreams').reduce((acc, item) => {
        return (!acc && item && item.count && item.count() >= 0) || acc;
      }, false);
    };

    return this.props.submitFailed && isAnyDataStreamHasError() && (<Icon type="exclamation-circle-o" className="product-tab-invalid"/>) || null;
  }

  productMetadataInvalidIcon() {

    const isAnyMetaFieldHasError = () => {
      if(!this.props.formSyncErrors.has('metaFields'))
        return false;

      return this.props.formSyncErrors.get('metaFields').reduce((acc, item) => {
        return (!acc && item && item.count && item.count() >= 0) || acc;
      }, false);
    };

    return this.props.submitFailed && isAnyMetaFieldHasError() && (<Icon type="exclamation-circle-o" className="product-tab-invalid"/>) || null;
  }

  productEventsInvalidIcon() {
    const isAnyEventHasError = () => {
      if(!this.props.formSyncErrors.has('events'))
        return false;

      return this.props.formSyncErrors.get('events').reduce((acc, item) => {
        return (!acc && item && item.count && item.count() >= 0) || acc;
      }, false);
    };

    return this.props.submitFailed && isAnyEventHasError() && (<Icon type="exclamation-circle-o" className="product-tab-invalid"/>) || null;
  }

  productDashboardInvalidIcon() {
    const isAnyWidgetHasError = () => {
      if(!this.props.formSyncErrors.has('webDashboard'))
        return false;

      return this.props.formSyncErrors.get('webDashboard').reduce((acc, item) => {
        return (!acc && item && item.count && item.count() >= 0) || acc;
      }, false);
    };

    return this.props.submitFailed && isAnyWidgetHasError() && (<Icon type="exclamation-circle-o" className="product-tab-invalid"/>) || null;
  }

  render() {

    return (

        <div>
          <MainLayout.Header title={this.props.initialValues.name}
                             options={(

                               <div>
                                 <Button type="danger" onClick={this.toggleDelete}>Delete</Button>
                                 <Button type="default"
                                         onClick={this.props.onCancel}>
                                   Cancel
                                 </Button>
                                 <Button type="primary"
                                         onClick={this.handleSubmit}
                                         loading={this.props.submitting}
                                         disabled={this.props.dirty === false || (this.props.submitFailed && this.props.invalid)}>
                                   Save
                                 </Button>
                               </div>

                             )}
          />
          <MainLayout.Content className="product-edit-content">
            {/*{this.state.activeTab === TABS.METADATA && <Popover*/}
              {/*placement="bottomRight"*/}
              {/*content={<MetadataIntroductionMessage onGotItClick={this.toggleMetadataIntroductionMessage}/>}*/}
              {/*visible={this.isMetadataIntroductionMessageVisible()}*/}
              {/*overlayClassName="products-metadata-introduction-message-popover"*/}
              {/*trigger="click">*/}

              {/*<Icon type="info-circle" className="products-metadata-info"*/}
                    {/*onClick={this.toggleMetadataIntroductionMessage}/>*/}
            {/*</Popover>}*/}

            <Tabs defaultActiveKey={TABS.INFO}
                  activeKey={this.props.activeTab}
                  onChange={this.handleTabChange} className="products-tabs">

              <Tabs.TabPane tab={<span>{this.productInfoInvalidIcon()}Info</span>} key={TABS.INFO}>
                <InfoTab />
              </Tabs.TabPane>

              <Tabs.TabPane tab={<span>{this.productMetadataInvalidIcon()}Metadata</span>} key={TABS.METADATA} forceRender={true}>
                <FieldArray name={`metaFields`} component={MetadataTab}/>
              </Tabs.TabPane>

              <Tabs.TabPane tab={<span>{this.productDataStreamsInvalidIcon()}Data Streams</span>} key={TABS.DATA_STREAMS} forceRender={true}>
                <FieldArray name={`dataStreams`} component={DataStreamsTab}/>
                {/*</>*/}
              </Tabs.TabPane>

              <Tabs.TabPane tab={<span>{this.productEventsInvalidIcon()}Events</span>} key={TABS.EVENTS} forceRender={true}>
                <FieldArray component={EventsTab} name={`events`}/>
              </Tabs.TabPane>

              <Tabs.TabPane tab={<span>{this.productDashboardInvalidIcon()}Dashboard</span>} key={TABS.DASHBOARD} forceRender={true}>
                <FieldArray component={DashboardTab} name={`webDashboard.widgets`}/>
              </Tabs.TabPane>

            </Tabs>

            {/*<DeleteModal deviceCount={this.state.currentProduct.deviceCount} onCancel={this.toggleDelete}*/}
                         {/*visible={this.state.showDeleteModal} handleSubmit={this.handleDeleteSubmit}*/}
                         {/*productName={this.state.currentProduct.name}/>*/}

            <ProductDevicesForceUpdate
              isModalVisible={this.props.isDevicesForceUpdateVisible}
              loading={this.props.deviceForceUpdateLoading}
              product={this.props.initialValues}
              onSave={this.props.onDevicesForceUpdateSubmit}
              onCancel={this.props.onDevicesForceUpdateCancel}/>

          </MainLayout.Content>
        </div>

    );
  }
}

export default ProductEdit;
