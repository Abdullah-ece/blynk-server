import React from 'react';

import {Icon, Tooltip, Upload, message} from 'antd';

import classnames from 'classnames';

import './styles.less';

class LogoUploader extends React.Component {

  static propTypes = {
    logo: React.PropTypes.string,
    onChange: React.PropTypes.func
  };

  constructor(props) {
    super(props);

    this.hide = null;
  }

  tooltip = <span>Recommended 5:1 ratio, 1mb size</span>;

  draggerProps = {
    name: 'file',
    action: '/dashboard/upload',
    showUploadList: false,
    accept: 'image/*'
  };

  onChange(info) {
    if (info.file.status === 'uploading' && this.hide === null) {
      this.hide = message.loading('Uploading image...', 0);
    }
    if (info.file.status !== 'uploading') {
      if (this.props.onChange) {
        this.props.onChange(info.file.response);
      }
    }
    if (info.file.status === 'done') {
      this.hide();
      message.success(`${info.file.name} file uploaded successfully`);
    } else if (info.file.status === 'error') {
      this.hide();
      message.error(`${info.file.name} file upload failed.`);
    }
  }

  render() {

    const uploaderClass = classnames({
      'logo-uploader-dropdown-zone': true,
      'contains-logo': !!this.props.logo
    });

    return (
      <div className="logo-uploader">
        <div className="logo-uploader-title">
          Logo
          <Tooltip placement="right" title={this.tooltip}>
            <Icon type="info-circle" className="logo-uploader-title-info"/>
          </Tooltip>
        </div>

        <div className={uploaderClass}>
          <Upload.Dragger {...this.draggerProps} onChange={this.onChange.bind(this)}>
            { this.props.logo && <img className="logo-uploader-dropdown-zone-logo"
                                      src={this.props.logo}
                                      alt=""/> }
            { !this.props.logo && <div>
              <p className="ant-upload-drag-icon">
                <Icon type="cloud-upload-o"/>
              </p>
              <p className="ant-upload-text">Add logo</p>
            </div>
            }
          </Upload.Dragger>
        </div>

      </div>
    );
  }
}

export default LogoUploader;
