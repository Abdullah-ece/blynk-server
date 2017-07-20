import React      from 'react';
import {Link}     from 'react-router';
import {Button}   from 'antd';
import PropTypes  from 'prop-types';
import './styles.less';

class MainEmpty extends React.Component {

  static propTypes = {
    title: PropTypes.string,
    description: PropTypes.string,
    link: PropTypes.string,
    btnText: PropTypes.string
  };

  render() {
    return (
      <div className="main-empty">
        { this.props.title && (
          <div className="main-empty-title">{this.props.title}</div>
        )}
        { this.props.description && (
          <div className="main-empty-description">{this.props.description}</div>
        )}
        { this.props.btnText && (
          <div className="main-empty-action">
            { this.props.link && (
              <Link to={this.props.link}>
                <Button icon="plus" type="primary">{this.props.btnText}</Button>
              </Link>
            )}
            { !this.props.link && (
              <Button icon="plus" type="primary">{this.props.btnText}</Button>
            )}
          </div>
        )}
      </div>
    );
  }

}

export default MainEmpty;
