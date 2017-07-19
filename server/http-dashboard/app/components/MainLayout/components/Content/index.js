import React      from 'react';
import PropTypes  from 'prop-types';
import './styles.less';

class Content extends React.Component {

  static propTypes = {
    children: PropTypes.any
  };

  render() {
    return (
      <div className="main-layout--content">
        {this.props.children}
      </div>
    );
  }

}

export default Content;
