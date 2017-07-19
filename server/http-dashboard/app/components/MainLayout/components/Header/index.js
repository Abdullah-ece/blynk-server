import React      from 'react';
import Dotdotdot  from 'react-dotdotdot';
import './styles.less';

class Header extends React.Component {

  static propTypes = {
    title: React.PropTypes.oneOfType([
      React.PropTypes.string,
      React.PropTypes.object
    ]),
    options: React.PropTypes.object
  };

  render() {
    return (
      <div className="main-layout--header">
        <div className="main-layout--header-name">
          <Dotdotdot clamp={1}>
            {this.props.title}
          </Dotdotdot>
        </div>
        <div className="main-layout--header-options">
          {this.props.options}
        </div>
      </div>
    );
  }

}

export default Header;
