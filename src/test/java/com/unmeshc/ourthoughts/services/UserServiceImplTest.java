package com.unmeshc.ourthoughts.services;

public class UserServiceImplTest {
/*
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserCommandToUser userCommandToUser;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void isEmailExistsTrue() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        assertThat(service.isEmailExists(anyString())).isTrue();
    }

    @Test
    public void isEmailExistsFalse() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        assertThat(service.isEmailExists(anyString())).isFalse();
    }

    @Test
    public void saveUser() {
        Role role = Role.builder().name("USER").build();
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = User.builder().password("unmesh").roles(roles).active(true).build();
        when(userCommandToUser.convert(any(UserCommand.class))).thenReturn(
                User.builder().password("unmesh").build());
        when(passwordEncoder.encode(anyString())).thenReturn("unmesh");
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = service.saveUser(UserCommand.builder().build());

        assertThat(savedUser).isNotNull();
        verify(userCommandToUser).convert(any(UserCommand.class));
        verify(passwordEncoder).encode(anyString());
        verify(roleRepository).findByName(anyString());
        verify(userRepository).save(user);
    }

    @Test(expected = RuntimeException.class)
    public void saveUserRoleNotFound() {
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());

        service.saveUser(UserCommand.builder().build());
    }*/
}