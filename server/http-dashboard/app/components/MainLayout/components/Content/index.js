import React        from 'react';
import PropTypes    from 'prop-types';
import classnames   from 'classnames';
import './styles.less';

class Content extends React.Component {

  static propTypes = {
    children: PropTypes.any,
    style: PropTypes.object,
    className: PropTypes.string,
  };

  render() {

    const className = classnames({
      [this.props.className]: !!String(this.props.className),
      'main-layout--content': true
    });

    return (
      <div className={className} style={this.props.style}>
        {this.props.children}
      </div>
    );
  }

}

export default Content;
