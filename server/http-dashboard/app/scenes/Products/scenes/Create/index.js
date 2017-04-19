import React from 'react';
import './styles.less';
import {Button, Tabs, Icon, Popover} from 'antd';
import MetadataIntroductionMessage from "./components/MetadataIntroductionMessage/index";
import ProductCreateInfoTab from './scenes/Info';
import ProductCreateMetadataTab from './scenes/Metadata';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {ProductsUpdateMetadataInfoRead} from 'data/Storage/actions';

@connect((state) => ({
  isMetadataInfoRead: state.Storage.products.isMetadataInfoRead
}), (dispatch) => ({
  updateMetadataInfoReadFlag: bindActionCreators(ProductsUpdateMetadataInfoRead, dispatch)
}))
class ProductCreate extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    params: React.PropTypes.object,
    isMetadataInfoRead: React.PropTypes.bool,
    updateMetadataInfoReadFlag: React.PropTypes.func
  };

  constructor(props) {
    super(props);

    this.state = {
      activeTab: props.params.tab || this.TABS.INFO,
      metadataIntroVisible: false
    };
  }

  TABS = {
    INFO: 'info',
    METADATA: 'metadata',
    DATA_STREAMS: 'datastreams',
    EVENTS: 'events'
  };


  handleTabChange(key) {
    this.setState({
      activeTab: key
    });

    this.context.router.push(`/products/create/${key}`);
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
      this.props.updateMetadataInfoReadFlag(true);
      this.setState({
        metadataIntroVisible: false
      });
    }
  }

  render() {

    return (
      <div className="products-create">
        <div className="products-header">
          <div className="products-header-name">New Product</div>
          <div className="products-header-options">
            <Button type="default">Cancel</Button>
            <Button type="primary">Save</Button>
          </div>
        </div>
        <div className="products-content" style={{position: 'relative'}}>
          { this.state.activeTab === this.TABS.METADATA && <Popover
            placement="bottomRight"
            content={<MetadataIntroductionMessage onGotItClick={this.toggleMetadataIntroductionMessage.bind(this)}/>}
            visible={this.isMetadataIntroductionMessageVisible()}
            overlayClassName="products-metadata-introduction-message-popover"
            trigger="click">

            <Icon type="info-circle" className="products-metadata-info"
                  onClick={this.toggleMetadataIntroductionMessage.bind(this)}/>
          </Popover>}

          <Tabs defaultActiveKey={this.TABS.INFO} activeKey={this.state.activeTab}
                onChange={this.handleTabChange.bind(this)} className="products-tabs">
            <Tabs.TabPane tab="Info" key={this.TABS.INFO}>
              <ProductCreateInfoTab />
            </Tabs.TabPane>
            <Tabs.TabPane tab="Metadata" key={this.TABS.METADATA}>
              <ProductCreateMetadataTab />
            </Tabs.TabPane>
            <Tabs.TabPane tab="Data Streams" key={this.TABS.DATA_STREAMS}>Content of Data Streams</Tabs.TabPane>
            <Tabs.TabPane tab="Events" key={this.TABS.EVENTS}>Content of Events</Tabs.TabPane>
          </Tabs>
        </div>
      </div>
    );
  }
}

export default ProductCreate;
