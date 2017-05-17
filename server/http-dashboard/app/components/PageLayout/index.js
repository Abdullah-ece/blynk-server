import React from 'react';
import {Layout} from 'antd';
import {getChildrenByType} from 'services/Layout';
import {Content as PageLayoutContent, Navigation as PageLayoutNavigation} from './components';

const {Sider, Content} = Layout;

import './styles.less';

class PageLayout extends React.Component {

  static propTypes = {
    children: React.PropTypes.any
  };

  render() {
    return (
      <Layout className="page-layout">
        <Layout>
          <Sider width={400} className="page-layout-navigation">
            {getChildrenByType(PageLayoutNavigation.displayName, this.props.children)}
          </Sider>
          <Content className="page-layout-content">
            {getChildrenByType(PageLayoutContent.displayName, this.props.children)}
          </Content>
        </Layout>
      </Layout>
    );
  }

}

PageLayout.Content = PageLayoutContent;
PageLayout.Navigation = PageLayoutNavigation;

export default PageLayout;
