import React from 'react';
import {getChildrenByType} from 'services/Layout';
import {Content as PageLayoutContent, Navigation as PageLayoutNavigation} from './components';

import './styles.less';

class PageLayout extends React.Component {

  static propTypes = {
    children: React.PropTypes.any
  };

  render() {
    return (
      <div className="page-layout">
        <div className="page-layout-navigation">
            {getChildrenByType(PageLayoutNavigation.displayName, this.props.children)}
        </div>
        <div className="page-layout-content">
            {getChildrenByType(PageLayoutContent.displayName, this.props.children)}
        </div>
      </div>
    );
  }

}

PageLayout.Content = PageLayoutContent;
PageLayout.Navigation = PageLayoutNavigation;

export default PageLayout;
