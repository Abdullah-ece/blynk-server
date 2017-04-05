import React from 'react';

import {Icon, Tooltip, Upload} from 'antd';

import './styles.scss';

class LogoUploader extends React.Component {

  tooltip = <span>Recommended 5:1 ratio, 1mb size</span>;

  draggerProps = {
    name: 'file',
    multiple: false,
  };

  render() {
    return (
      <div className="logo-uploader">
        <div className="logo-uploader-title">
          Logo
          <Tooltip placement="right" title={this.tooltip}>
            <Icon type="info-circle" className="logo-uploader-title-info"/>
          </Tooltip>
        </div>

        <div className="logo-uploader-dropdown-zone">
          <Upload.Dragger {...this.draggerProps}>
            <p className="ant-upload-drag-icon">
              <Icon type="cloud-upload-o"/>
            </p>
            <p className="ant-upload-text">Add logo</p>
          </Upload.Dragger>
        </div>

      </div>
    );
  }
}

export default LogoUploader;
