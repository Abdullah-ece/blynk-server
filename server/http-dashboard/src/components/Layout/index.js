import React from 'react';

import './ant-overrides.less';

import './styles.scss';

export default class Layout extends React.Component {

  static propTypes = {
    children: React.PropTypes.object
  };

  render() {
    return (
      <div className="layout">
        { this.props.children }
      </div>
    );
  }

}
