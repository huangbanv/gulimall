package com.zhangjun.gulimall.member.service.impl;

import com.zhangjun.gulimall.member.dao.MemberLevelDao;
import com.zhangjun.gulimall.member.entity.MemberLevelEntity;
import com.zhangjun.gulimall.member.exception.MailExistException;
import com.zhangjun.gulimall.member.exception.UsernameExistException;
import com.zhangjun.gulimall.member.vo.MemberLoginVo;
import com.zhangjun.gulimall.member.vo.MemberRegisterVo;
import com.zhangjun.gulimall.member.vo.SocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangjun.common.utils.PageUtils;
import com.zhangjun.common.utils.Query;

import com.zhangjun.gulimall.member.dao.MemberDao;
import com.zhangjun.gulimall.member.entity.MemberEntity;
import com.zhangjun.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberRegisterVo memberRegisterVo) {
        checkEmailUnique(memberRegisterVo.getMail());
        checkUsernameUnique(memberRegisterVo.getUserName());
        MemberEntity member = new MemberEntity();
        MemberLevelEntity memberLevelEntity =  memberLevelDao.getDefaultLevel();
        member.setLevelId(memberLevelEntity.getId());
        member.setEmail(memberRegisterVo.getMail());
        member.setUsername(memberRegisterVo.getUserName());
        member.setNickname(memberRegisterVo.getUserName());
        String encode = new BCryptPasswordEncoder().encode(memberRegisterVo.getPassword());
        member.setPassword(encode);
        this.baseMapper.insert(member);
    }

    @Override
    public void checkEmailUnique(String email) throws MailExistException{
        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("email", email));
        if(count>0){
            throw new MailExistException();
        }
    }

    @Override
    public void checkUsernameUnique(String userName) throws UsernameExistException{
        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if(count>0){
            throw new UsernameExistException();
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginAccount = vo.getLoginAccount();
        String password = vo.getPassword();
        MemberEntity entity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().
                eq("username", loginAccount).or().eq("email", loginAccount));
        if(entity == null){
            return null;
        }else {
            String passwordDb = entity.getPassword();
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            boolean matches = bCryptPasswordEncoder.matches(password, passwordDb);
            if(matches){
                return entity;
            }else {
                return null;
            }
        }
    }

    @Override
    public MemberEntity login(SocialUser vo) {
        String id = vo.getId();
        MemberEntity entity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("gitee_id", id));
        if(entity != null){
            MemberEntity update = new MemberEntity();
            update.setId(entity.getId());
            update.setAccessToken(vo.getAccess_token());
            update.setExpiresIn(vo.getExpires_in());
            this.baseMapper.updateById(update);
            entity.setAccessToken(vo.getAccess_token());
            entity.setExpiresIn(vo.getExpires_in());
            return entity;
        }else {
            MemberEntity register = new MemberEntity();
            register.setUsername(vo.getName());
            register.setNickname(vo.getName());
            register.setEmail(vo.getEmail());
            try {
                checkEmailUnique(vo.getEmail());
            }catch (MailExistException e){
                register.setEmail(null);
            }
            MemberLevelEntity memberLevelEntity =  memberLevelDao.getDefaultLevel();
            register.setLevelId(memberLevelEntity.getId());
            register.setExpiresIn(vo.getExpires_in());
            register.setAccessToken(vo.getAccess_token());
            register.setGiteeId(id);
            this.baseMapper.insert(register);
            return register;
        }
    }
}