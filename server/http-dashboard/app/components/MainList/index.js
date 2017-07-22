import React        from 'react';
import PropTypes    from 'prop-types';
import classnames   from 'classnames';
import {
  MainItem,
  MainEmpty
}                   from './components';
import './styles.less';

class MainList extends React.Component {

  static propTypes = {
    children: PropTypes.any,
    className: PropTypes.string
  };

  render() {

    const className = classnames({
      'main-list': true,
      [this.props.className]: !!(this.props.className && this.props.className.length)
    });

    return (
      <div className={className}>
        { this.props.children }
      </div>
    );
  }

}

MainList.Item = MainItem;
MainList.Empty = MainEmpty;

export default MainList;
