import React                      from 'react';
import {Input, Item, Switch}      from "components/UI";
import Validation                 from 'services/Validation';
import {Row, Col, message}        from 'antd';
import {
  Field as FormField
}                                 from 'redux-form';
import ImageUploader              from 'components/ImageUploader';
import PropTypes                  from 'prop-types';

class Form extends React.Component {

  static propTypes = {
    organizationName: PropTypes.string
  };

  constructor(props) {
    super(props);
  }

  logoComponent({input, meta: {error, touched}}) {

    const handleComponentChange = (info) => {
      const status = info.file.status;
      if (status === 'done') {
        input.onChange(info.file.response);
      } else if (status === 'error') {
        message.error(`${info.file.name} file upload failed.`);
      }
    };

    return (
      <ImageUploader text="Add image"
                     logo={input.value}
                     error={error}
                     touched={touched}
                     hint={() => (
                       <span>Upload from computer or drag-n-drop<br/>.png or .jpg, min 500x500px</span>
                     )}
                     onChange={handleComponentChange}/>
    );
  }

  render() {
    return (
      <Row gutter={24}>
        <Col span={15}>

          <Item label="Name" offset="medium">
            <Input name="name" placeholder="Name" validate={[Validation.Rules.required]}/>
          </Item>

          <Item label="Description" offset="normal">
            <Input name="description" type="textarea" rows="5" placeholder="Description (optional)"/>
          </Item>

          <Item>
            <Switch size="small" name="canCreateOrgs"
                    label={`${this.props.organizationName} can create Sub-Organizations`}/>
          </Item>

        </Col>
        <Col span={9}>
          <div className="organizations-create-drag-and-drop">
            <FormField name="logoUrl"
                       component={this.logoComponent}/>
          </div>
        </Col>
      </Row>
    );
  }

}

export default Form;
