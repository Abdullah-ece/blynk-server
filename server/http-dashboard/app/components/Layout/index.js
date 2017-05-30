import React from 'react';

import './ant-overrides.less';

import './styles.less';
import {PageLoading} from 'components';

export UserLayout from './components/UserLayout';
export UserProfileLayout from './components/UserProfileLayout';

export default class Layout extends React.Component {

  static propTypes = {
    children: React.PropTypes.object
  };

  render() {
    return (
      <div className="layout">
        <PageLoading />
        { this.props.children }
      </div>
    );
  }

}
