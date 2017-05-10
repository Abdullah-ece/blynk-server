import React from 'react';

import {Icon, Tooltip} from 'antd';
import ImageUploader from 'components/ImageUploader';

import classnames from 'classnames';

import './styles.less';

class LogoUploader extends React.Component {

  static propTypes = {
    logo: React.PropTypes.string,
    defaultImage: React.PropTypes.string,
    onChange: React.PropTypes.func
  };

  constructor(props) {
    super(props);
  }

  tooltip = <span>Recommended 5:1 ratio, 1Mb size</span>;

  draggerProps = {
    name: 'file',
    action: '/dashboard/upload',
    showUploadList: false,
    accept: 'image/*'
  };

  onChange(info) {
    if (info.file.status === 'done') {
      if (this.props.onChange) {
        this.props.onChange(info.file.response);
      }
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
          <ImageUploader text="Add Logo" onChange={this.onChange.bind(this)} logo={this.props.logo}
                         defaultImage={this.props.defaultImage}/>
        </div>

      </div>
    );
  }
}

export default LogoUploader;
