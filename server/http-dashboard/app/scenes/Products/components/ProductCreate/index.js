import React from 'react';
import {
  Button,
  Tabs,
  Icon,
  // Popover
} from 'antd';
import {MainLayout} from 'components';
import {
  TABS,
  // FORMS,
  // PRODUCT_CREATE_INITIAL_VALUES,
} from 'services/Products';
import {
  Info        as InfoTab,
  Events      as EventsTab,
  Metadata    as MetadataTab,
  DataStreams as DataStreamsTab,
  Dashboard   as DashboardTab,
} from '../ProductManage';

// import DashboardTab                         from 'scenes/Products/scenes/Dashboard';
// import MetadataIntroductionMessage          from '../MetadataIntroductionMessage';

import {
  // HARDWARES,
  // CONNECTIONS_TYPES,
  AVAILABLE_HARDWARE_TYPES_LIST,
  AVAILABLE_CONNECTION_TYPES_LIST,
} from 'services/Devices';


import PropTypes from 'prop-types';

import ImmutablePropTypes from 'react-immutable-proptypes';

import {
  reduxForm,
  FieldArray
} from 'redux-form';

@reduxForm({
  shouldValidate: () => true,
  validate: (fields) => {

    let validationErrors = {
      metaFields: {}
    };

    const UNIQUE_NAME_ERROR = 'Name should be unique';

    if(fields && fields.metaFields && fields.metaFields.length)
      fields.metaFields.forEach((field1, index1) => {

        fields.metaFields.forEach((field2, index2) => {
          if(index1 !== index2 && field1.name && field2.name && field1.name.trim() === field2.name.trim()) {
            validationErrors.metaFields[index1] = { name: UNIQUE_NAME_ERROR };
            validationErrors.metaFields[index2] = { name: UNIQUE_NAME_ERROR };
          }
        });

      });

    return validationErrors;
  },
})
class ProductCreate extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {

    formValues: ImmutablePropTypes.contains({
      name: PropTypes.string,
      boardType: PropTypes.oneOf(AVAILABLE_HARDWARE_TYPES_LIST),
      connectionType: PropTypes.oneOf(AVAILABLE_CONNECTION_TYPES_LIST),
      description: PropTypes.string,
      logoUrl: PropTypes.string,
      metaFields: ImmutablePropTypes.list,
    }),

    formSyncErrors: PropTypes.object,

    isProductClone: PropTypes.bool,

    loading: PropTypes.bool,
    invalid: PropTypes.bool,
    submitting: PropTypes.bool,
    submitFailed: PropTypes.bool,

    onCancel: PropTypes.func,
    onSubmit: PropTypes.func,
    handleSubmit: PropTypes.func,

    params: PropTypes.shape({
      tab: PropTypes.string
    }),

    // handleCancel: React.PropTypes.func,
    // handleSubmit: React.PropTypes.func,
    // onInfoValuesChange: React.PropTypes.func,
    // onEventsFieldsChange: React.PropTypes.func,
    // onMetadataFieldChange: React.PropTypes.func,
    // onMetadataFieldsChange: React.PropTypes.func,
    // onDataStreamsFieldChange: React.PropTypes.func,
    // onDataStreamsFieldsChange: React.PropTypes.func,
    // updateMetadataFirstTimeFlag: React.PropTypes.func,
    //
    // isMetadataInfoRead: React.PropTypes.bool,
    // isInfoFormInvalid: React.PropTypes.bool,
    // isEventsFormInvalid: React.PropTypes.bool,
    // isMetadataFormInvalid: React.PropTypes.bool,
    // isDataStreamsFormInvalid: React.PropTypes.bool,
    //
    // product: React.PropTypes.object,
    // loading: React.PropTypes.bool,
  };

  constructor(props) {
    super(props);

    this.state = {
      //   originalName: null,
      //   submited: false,
      activeTab: props.params.tab || TABS.INFO,
      //   metadataIntroVisible: false
    };

    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleCancel = this.handleCancel.bind(this);

    this.handleTabChange = this.handleTabChange.bind(this);
    // this.toggleMetadataIntroductionMessage = this.toggleMetadataIntroductionMessage.bind(this);

  }

  componentWillMount() {
    // if (!this.state.originalName) {
    //   this.setState({
    //     originalName: this.props.product.info.values.name
    //   });
    // }
  }

  TABS = {
    INFO: 'info',
    METADATA: 'metadata',
    // DATA_STREAMS: 'datastreams',
    // EVENTS: 'events'
  };

  // isMetadataIntroductionMessageVisible() {
  //   if (!this.props.isMetadataInfoRead) return true;
  //
  //   return this.state.metadataIntroVisible;
  // }


  // toggleMetadataIntroductionMessage() {
  //
  //   this.setState({
  //     metadataIntroVisible: !this.state.metadataIntroVisible,
  //   });
  //
  //   if (!this.props.isMetadataInfoRead) {
  //     this.props.updateMetadataFirstTimeFlag(false);
  //     this.setState({
  //       metadataIntroVisible: false
  //     });
  //   }
  // }

  handleTabChange(key) {
    this.setState({
      activeTab: key
    });
  }

  // isInfoFormInvalid() {
  //   return this.props.isInfoFormInvalid;
  // }
  //
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

  handleCancel() {
    this.props.onCancel();
  }

  handleSubmit() {
    this.props.onSubmit();
  }

  render() {

    return (
      <MainLayout>
        <MainLayout.Header title={this.props.formValues.get('name') || 'New Product'}
                           options={(
                             <div>
                               <Button type="default"
                                       onClick={this.handleCancel}>
                                 Cancel
                               </Button>
                               <Button type="primary"
                                       onClick={this.props.handleSubmit}
                                       loading={this.props.loading}
                                       disabled={(this.props.submitFailed && this.props.invalid) || this.props.submitting}>
                                 {this.props.isProductClone ? "Clone" : "Create"}
                               </Button>
                             </div>
                           )}
        />
        <MainLayout.Content className="product-create-content">

          <Tabs className="products-tabs"
                defaultActiveKey={TABS.INFO}
                onChange={this.handleTabChange}
                activeKey={this.state.activeTab}>

            <Tabs.TabPane tab={<span>{this.productInfoInvalidIcon()}Info</span>} key={TABS.INFO}>
              <InfoTab/>
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
              <FieldArray component={DashboardTab} name={`webDashboard.widgets`} isDevicePreviewEnabled={false}/>
            </Tabs.TabPane>

          </Tabs>

        </MainLayout.Content>
      </MainLayout>
    );
  }
}

export default ProductCreate;
