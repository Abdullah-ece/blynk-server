import React from 'react';
import {Upload, Icon, message} from 'antd';
import classnames from 'classnames';
import './styles.less';

class ImageUploader extends React.Component {

  static propTypes = {
    onChange: React.PropTypes.func,
    text: React.PropTypes.any,
    error: React.PropTypes.any,
    touched: React.PropTypes.any,
    hint: React.PropTypes.any,
    logo: React.PropTypes.string
  };

  constructor(props) {
    super(props);

    this.message = null;
  }

  onChange(info) {

    if (info.file.status === 'uploading') {
      if (!this.message) {
        this.message = message.loading('Uploading image...');
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
        response: null
      }
    });

  }

  fileProps = {
    name: 'file',
    action: '/dashboard/upload',
    showUploadList: false,
    accept: 'image/*'
  };

  render() {

    const classNames = classnames({
      'image-uploader': true,
      'image-uploader-error': this.props.touched && this.props.error
    });

    return (
      <div className={classNames}>
        <Upload.Dragger {...this.fileProps} onChange={this.onChange.bind(this)}>
          { this.props.logo && <div className="image-uploader-cover">
            <div className="image-uploader-cover-tools">
              <Icon type="delete" onClick={this.handleDelete.bind(this)}/>
            </div>
            <img src={this.props.logo}
                 alt=""/>
          </div> }
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
        { this.props.error && this.props.touched && (
          <div className="image-uploader-error">
            { this.props.error }
          </div>
        )}
      </div>
    );
  }
}

export default ImageUploader;
