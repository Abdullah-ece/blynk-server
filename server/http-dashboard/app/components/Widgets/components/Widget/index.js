import React from 'react';
import {Button} from 'antd';
import Dotdotdot from 'react-dotdotdot';
import PropTypes from 'prop-types';
import './styles.less';

class Widget extends React.Component {

  static propTypes = {
    children: PropTypes.oneOfType([
      PropTypes.element,
      PropTypes.array,
    ])
  };

  render() {

    return (
      <div {...this.props} className="widgets--widget">
        <div className="widgets--widget-label">
          <Dotdotdot clamp={1}>Widget is there</Dotdotdot>
          <div className="widgets--widget-tools">
            <Button icon="delete" size="small"/>
            <Button icon="copy" size="small"/>
            <Button icon="setting" size="small"/>
          </div>
        </div>
        {this.props.children}
      </div>
    );
  }

}

export default Widget;
