import React      from 'react';
import PropTypes  from 'prop-types';
import {
  MainItem,
  MainEmpty
}                 from './components';
import './styles.less';

class MainList extends React.Component {

  static propTypes = {
    children: PropTypes.any
  };

  render() {
    return (
      <div className="main-list">
        { this.props.children }
      </div>
    );
  }

}

MainList.Item = MainItem;
MainList.Empty = MainEmpty;

export default MainList;
