import React from 'react';

import UI from 'components/UI';
import Validation from 'services/Validation';

import {Button, Input, Checkbox, Select, Radio} from 'antd';

import './styles.less';

class StyleGuide extends React.Component {
  render() {
    return (
      <div className="style-guide">
        <div className="style-guide-element">
          <Button type="primary">Button</Button> <Button type="primary" disabled>Button</Button>
        </div>
        <div className="style-guide-element">
          <Button type="primary" icon="plus">Button</Button> <Button type="primary" icon="plus" disabled>Button</Button>
        </div>
        <div className="style-guide-element">
          <Button>Button</Button> <Button disabled>Button</Button>
        </div>
        <div className="style-guide-element">
          <Button icon="edit"/>
        </div>
        <div className="style-guide-element dark">
          <Button icon="edit" className="dark"/> <Button icon="user" className="dark"/>
        </div>
        <div className="style-guide-element">
          <Button type="danger">Button</Button> <Button type="danger" disabled>Button</Button>
        </div>
        <div className="style-guide-element">
          <Button type="dashed" icon="plus">Add Meta data</Button>
        </div>
        <div className="style-guide-element">
          <Input placeholder="Label" style={{width: 168}}/>
        </div>
        <div className="style-guide-element">
          <Input placeholder="Label" style={{width: 168}} disabled/>
        </div>
        <div className="style-guide-element has-error">
          <Input placeholder="Label" style={{width: 168}}/>
        </div>
        <div className="style-guide-element">
          <Checkbox />
        </div>
        <div className="style-guide-element">
          <Checkbox>Check me</Checkbox>
        </div>
        <div className="style-guide-element">
          <Checkbox disabled>Check me</Checkbox>
        </div>
        <div className="style-guide-element">
          <Radio>Radio</Radio>
        </div>
        <div className="style-guide-element">
          <Radio disabled>Radio</Radio>
        </div>
        <div className="style-guide-element">
          <Select defaultValue="lucy" style={{width: 120}}>
            <Select.Option value="jack">Jack</Select.Option>
            <Select.Option value="lucy">Lucy</Select.Option>
            <Select.Option value="disabled" disabled>Disabled</Select.Option>
            <Select.Option value="Yiminghe">yiminghe</Select.Option>
          </Select>
        </div>
        <div className="style-guide-element">
          <Select defaultValue="lucy" style={{width: 120}} disabled>
            <Select.Option value="jack">Jack</Select.Option>
            <Select.Option value="lucy">Lucy</Select.Option>
            <Select.Option value="disabled" disabled>Disabled</Select.Option>
            <Select.Option value="Yiminghe">yiminghe</Select.Option>
          </Select>
        </div>
        <div className="style-guide-element">
          <a href="javascript:void(0);">Link</a>
        </div>
        <div className="style-guide-element">
          <UI.Form layout="vertical" form="style-guide">
            <UI.Form.Item>
              <UI.Form.Input name="something" validate={[Validation.Rules.required]}/>
            </UI.Form.Item>
            <UI.Form.Item label="Name">
              <UI.Form.Input name="name" validate={[Validation.Rules.required]}/>
            </UI.Form.Item>
          </UI.Form>
        </div>
      </div>
    );
  }
}

export default StyleGuide;
