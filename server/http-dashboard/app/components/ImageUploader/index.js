import React from 'react';
import { Upload, message } from 'antd';
import { LinearIcon } from 'components';
import classnames from 'classnames';
import { FILE_UPLOAD_URL } from 'services/API';
import './styles.less';

class ImageUploader extends React.Component {

  static propTypes = {
    onChange: React.PropTypes.func,
    text: React.PropTypes.any,
    error: React.PropTypes.any,
    fileProps: React.PropTypes.any,
    touched: React.PropTypes.any,
    hint: React.PropTypes.any,
    logo: React.PropTypes.string,
    defaultImage: React.PropTypes.string,
    iconClass: React.PropTypes.string,
  };

  constructor(props) {
    super(props);

    this.message = null;
  }

  onChange(info) {

    if (info.file.status === 'uploading') {
      if (!this.message) {
        this.message = message.loading('Uploading file...');
      }
    } else if (info.file.status === 'done') {
      this.message();
      this.message = null;
    }

    this.props.onChange(info);
  }

  handleDelete(e) {
    e.stopPropagation();

    this.props.onChange({
      file: {
        status: 'done',
        response: this.props.defaultImage || null
      }
    });

  }

  fileProps = {
    name: 'file',
    action: FILE_UPLOAD_URL,
    showUploadList: false,
    accept: 'image/*'
  };

  render() {

    const classNames = classnames({
      'image-uploader': true,
      'image-uploader-error': this.props.touched && this.props.error
    });

    const fileProps = {
      ...this.fileProps,
      ...this.props.fileProps,
    };

    return (
      <div className={classNames}>
        <Upload.Dragger {...fileProps} onChange={this.onChange.bind(this)}>
          {this.props.logo && (
            <div className="image-uploader-cover">
              {this.props.logo !== this.props.defaultImage && (
                <div className="image-uploader-cover-tools" onClick={this.handleDelete.bind(this)}>
                  <LinearIcon type="trash"/>
                </div>
              )}
              <img src={this.props.logo}
                   alt=""/>
            </div>)
          }
          {!this.props.logo && <div>
            <p className={this.props.iconClass || 'ant-upload-drag-icon'}>
              <LinearIcon type="cloud-upload"/>
            </p>
            {this.props.text && <p
              className="ant-upload-text">{typeof this.props.text === 'function' ? this.props.text() : this.props.text} </p>}
            {this.props.hint && <p
              className="ant-upload-hint">{typeof this.props.hint === 'function' ? this.props.hint() : this.props.hint} </p>}
          </div>
          }
        </Upload.Dragger>
        {this.props.error && this.props.touched && (
          <div className="image-uploader-error">
            {this.props.error}
          </div>
        )}
      </div>
    );
  }
}

export default ImageUploader;
