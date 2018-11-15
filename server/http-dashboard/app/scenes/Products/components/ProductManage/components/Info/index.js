import React from 'react';
import FormItem from 'components/FormItem';
import {Input, Col, Row, message} from 'antd';
import {Field, Select} from 'components/Form';
import ImageUploader from 'components/ImageUploader';
import {Field as FormField} from 'redux-form';
import Validation from 'services/Validation';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {ProductInfoUpdateInvalidFlag} from 'data/Product/actions';
import {AVAILABLE_HARDWARE_TYPES, AVAILABLE_CONNECTION_TYPES} from 'services/Devices';
import {SecureTokenForUploadFetch} from "data/Product/api";

import './styles.less';

@connect((state) => ({
  secureUploadToken: state.Product.secureUploadToken,
}), (dispatch) => ({
  secureTokenForUploadFetch: bindActionCreators(SecureTokenForUploadFetch, dispatch),
  updateInfoInvalidFlag: bindActionCreators(ProductInfoUpdateInvalidFlag, dispatch)
}))
class Info extends React.Component {

  static propTypes = {
    secureUploadToken: React.PropTypes.string,
    updateInfoInvalidFlag: React.PropTypes.func,
    secureTokenForUploadFetch: React.PropTypes.func,
    invalid: React.PropTypes.bool
  };

  constructor(props) {
    super(props);

    this.invalid = false;

    this.fetchUploadToken = this.fetchUploadToken.bind(this);

    this.fetchUploadToken();
  }

  componentWillReceiveProps(props) {
    if (this.invalid !== props.invalid) {
      this.props.updateInfoInvalidFlag(props.invalid);
      this.invalid = props.invalid;
    }
  }

  fetchUploadToken() {
    this.props.secureTokenForUploadFetch();
  }

  hardware = AVAILABLE_HARDWARE_TYPES;

  connectionTypes = AVAILABLE_CONNECTION_TYPES;

  handleInfoFileChange(valueChanger, info) {
    const status = info.file.status;
    if (status === 'done') {
      this.fetchUploadToken();
      valueChanger(info.file.response);
    } else if (status === 'error') {
      this.fetchUploadToken();
      message.error(`${info.file.name} file upload failed.`);
    }
  }

  render() {

    const fileProps = {
      data: {
        token: this.props.secureUploadToken
      }
    };

    return (

      <Row gutter={24} className="products-create-tabs-inner-content">
        <Col span={15}>
          <FormItem>
            <FormItem.Title>Name</FormItem.Title>
            <FormItem.Content>
              <Field name="name" placeholder="Name" validate={[Validation.Rules.required]}/>
            </FormItem.Content>
          </FormItem>

          <FormItem>
            <FormItem.TitleGroup>
              <FormItem.Title style={{width: '50%'}}>hardware</FormItem.Title>
              <FormItem.Title style={{width: '50%'}}>connection type</FormItem.Title>
            </FormItem.TitleGroup>
            <FormItem.Content>
              <Input.Group compact>
                <Select style={{width: '50%'}} name="boardType" values={this.hardware}/>
                <Select style={{width: '50%'}} name="connectionType" values={this.connectionTypes}/>
              </Input.Group>
            </FormItem.Content>
          </FormItem>

          <FormItem>
            <FormItem.Title>description</FormItem.Title>
            <FormItem.Content>
              <div className="textarea-wrapper">
                <Field name="description" className="test" type="textarea" rows="5" placeholder="Description (optional)"/>
              </div>
            </FormItem.Content>
          </FormItem>
        </Col>
        <Col span={9}>
          <div className="products-create-drag-and-drop">
            <FormField name="logoUrl"
                       component={({input, meta: {error, touched}}) => (
                         <ImageUploader text="Add image"
                                        logo={input.value}
                                        error={error}
                                        touched={touched}
                                        fileProps={fileProps}
                                        hint={() => (
                                          <span>Upload from computer or drag-n-drop<br/>.png or .jpg, min 500x500px</span>
                                        )}
                                        onChange={this.handleInfoFileChange.bind(this, input.onChange)}/>
                       )}/>
          </div>
        </Col>
      </Row>
    );
  }
}

export default Info;
