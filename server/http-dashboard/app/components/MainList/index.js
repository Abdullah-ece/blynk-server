import React      from 'react';
import PropTypes  from 'prop-types';
import {MainItem} from './components';
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

export default MainList;
