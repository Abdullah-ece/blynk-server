import React from 'react';
import {Upload, Icon} from 'antd';
import './styles.less';

class ImageUploader extends React.Component {

  static propTypes = {
    onChange: React.PropTypes.func,
    text: React.PropTypes.any,
    hint: React.PropTypes.any,
    logo: React.PropTypes.string
  };

  fileProps = {
    name: 'file',
    action: '/dashboard/upload',
    showUploadList: false,
    accept: 'image/*'
  };

  render() {
    return (
      <Upload.Dragger className="image-uploader" {...this.fileProps} onChange={this.props.onChange.bind(this)}>
        { this.props.logo && <img src={this.props.logo}
                                  alt=""/> }
        { !this.props.logo && <div>
          <p className="ant-upload-drag-icon">
            <Icon type="cloud-upload-o"/>
          </p>
          {this.props.text && <p
            className="ant-upload-text">{typeof this.props.text === 'function' ? this.props.text() : this.props.text} </p>}
          {this.props.hint && <p
            className="ant-upload-hint">{typeof this.props.hint === 'function' ? this.props.hint() : this.props.hint} </p>}
        </div>
        }
      </Upload.Dragger>
    );
  }
}

export default ImageUploader;
