import React from 'react';

import './styles.less';

export default class ItemsGroup extends React.Component {
  static propTypes = {
    children: React.PropTypes.any
  }

  render() {
    return (
      <div className="ui-form-items-group">
        { this.props.children }
      </div>
    );
  }
}
